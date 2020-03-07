package org.ctavkep.pquest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ProductInfoActivity extends AppCompatActivity {
    private TextView productDesc;
    private TextView likeNumber;
    private TextView commentNumber;
    private ImageView productScreenshot;
    private Button getItButton;
    private String name;
    private String desc;
    private String screenshot;
    private String productUrl;
    private int upvote;
    private int comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.product_info_toolbar);
        productScreenshot = (ImageView) findViewById(R.id.product_screenshot);
        likeNumber = (TextView) findViewById(R.id.like_number);
        commentNumber = (TextView) findViewById(R.id.comment_number);
        productDesc = (TextView) findViewById(R.id.product_desc);
        getItButton = (Button) findViewById(R.id.get_button);

        setSupportActionBar(toolbar);
        getExtraData();
        getSupportActionBar().setTitle(name);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getExtraData() {
        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        desc = intent.getStringExtra("desc");
        screenshot = intent.getStringExtra("screenshot");
        productUrl = intent.getStringExtra("url");
        upvote = intent.getIntExtra("upvote", 0);
        comments = intent.getIntExtra("comments", 0);
    }

    private void initViews() {
        productDesc.setText(desc);
        Glide.with(this).load(screenshot).into(productScreenshot);
        likeNumber.setText(String.valueOf(upvote));
        commentNumber.setText(String.valueOf(comments));

        getItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(productUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
