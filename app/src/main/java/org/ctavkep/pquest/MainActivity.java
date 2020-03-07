package org.ctavkep.pquest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout productListSwipeRefresh;
    ListView productListItems;
    ProductAdapter productAdapter;
    Spinner toolbarSpinner;
    Toolbar toolbar;

    //temporary token may not work
    final String access_token = "591f99547f569b05ba7d8777e2e0824eea16c440292cce1f8dfb3952cc9937ff";

    ArrayList<String> productCategoryList = new ArrayList<>();
    ArrayList<Product> productItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parseProductCategories();
        findViews();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                productListSwipeRefresh.setRefreshing(false);
                if (toolbarSpinner.getSelectedItem() == null) {
                    return;
                }
                showPosts(toolbarSpinner.getSelectedItem().toString().toLowerCase());
            }
        }, 1000);
    }

    private void findViews() {
        productListItems = (ListView) findViewById(R.id.product_list_items);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarSpinner = (Spinner) findViewById(R.id.spinner_category);
        productListSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        productListSwipeRefresh.setOnRefreshListener(this);
        productListSwipeRefresh.setColorSchemeResources(R.color.primary);

        setSupportActionBar(toolbar);
        toolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString().toLowerCase();
                showPosts(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        productListItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ProductInfoActivity.class);
                intent.putExtra("name", productItems.get(position).productName);
                intent.putExtra("desc", productItems.get(position).productDesc);
                intent.putExtra("screenshot", productItems.get(position).productScreenshot);
                intent.putExtra("upvote", productItems.get(position).upvotes);
                intent.putExtra("comments", productItems.get(position).productComments);
                intent.putExtra("url", productItems.get(position).productUrl);
                startActivity(intent);
            }
        });
    }

    private void parseProductCategories() {
        Ion.with(getApplicationContext()).load("https://api.producthunt.com/v1/categories")
                .setHeader("Authorization", "Bearer " + access_token)
                .setLogging("Ion", Log.DEBUG)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(MainActivity.this, "Failed loading categories",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.w("links", result.toString());
                        if (result.has("categories")) {
                            JsonArray categoryArrayJSON = result.getAsJsonArray("categories");
                            for (int i = 0; i < categoryArrayJSON.size(); i++) {
                                JsonObject obj = categoryArrayJSON.get(i).getAsJsonObject();
                                productCategoryList.add(obj.get("name").toString()
                                        .replace("\"", ""));
                            }
                        }
                        chooseTechCategory();
                        fillCategoryAdapter();
                    }
                });
    }

    private void chooseTechCategory() {
        for (int i = 0; i < toolbarSpinner.getCount(); i++) {
            if (toolbarSpinner.getItemAtPosition(i).toString().equalsIgnoreCase("Tech")) {
                toolbarSpinner.setSelection(i);
                break;
            }
        }
    }

    private void fillCategoryAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item,
                productCategoryList);
        toolbarSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void showPosts(final String category){
        productItems.clear();
        Ion.with(getApplicationContext())
                .load("https://api.producthunt.com/v1/categories/" + category + "/posts")
                .setHeader("Authorization", "Bearer " + access_token)
                .setLogging("Ion", Log.DEBUG)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(MainActivity.this, "Failed loading posts in "
                                            + category + "category", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.w("links", result.toString());
                        if (result.has("posts")) {
                            JsonArray categoryArrayJSON = result.getAsJsonArray("posts");
                            for (int i = 0; i < categoryArrayJSON.size(); i++) {
                                JsonObject obj = categoryArrayJSON.get(i).getAsJsonObject();
                                JsonObject thumbnailObj = obj.getAsJsonObject("thumbnail");
                                JsonObject screenshotObj = obj.getAsJsonObject("screenshot_url");

                                int id = obj.get("id").getAsInt();

                                if (!existsId(productItems, id)) {
                                    productItems.add(new Product(obj.get("name").toString()
                                            .replace("\"", ""),
                                            obj.get("tagline").toString().replace("\"", ""),
                                            thumbnailObj.get("image_url").toString()
                                                    .replace("\"", ""),
                                            screenshotObj.get("300px").toString().replace("\"", ""),
                                            obj.get("discussion_url").toString().replace("\"", ""),
                                            obj.get("comments_count").getAsInt(),
                                            obj.get("category_id").getAsInt(),
                                            id,
                                            obj.get("votes_count").getAsInt()));
                                }
                            }
                            productAdapter = new ProductAdapter(MainActivity.this,
                                    R.layout.product_list_item, productItems);
                            productListItems.setAdapter(productAdapter);
                            productAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public static boolean existsId(ArrayList<Product> list, long id) {
        for (Product object : list) {
            if (object.getPostID() == id) {
                return true;
            }
        }
        return false;
    }
}