package org.ctavkep.pquest;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends ArrayAdapter {
    private Activity context;
    private List<Product> productList;

    public ProductAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        this.productList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.product_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.nameTV = convertView.findViewById(R.id.product_name);
            viewHolder.descTV = convertView.findViewById(R.id.product_desc);
            viewHolder.upvoteTV = convertView.findViewById(R.id.likes);
            viewHolder.commentsTV = convertView.findViewById(R.id.comments);
            viewHolder.thumbnailIV = convertView.findViewById(R.id.product_thumbnail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String tempName, tempDesc;
        tempName = productList.get(position).productName;
        tempDesc = productList.get(position).productDesc;
        if(tempName.length() > 30 ){
            tempName = tempName.substring(0, 30) + "...";
        }
        if(tempDesc.length() > 70 ){
            tempDesc = tempDesc.substring(0, 70) + "...";
        }
        viewHolder.nameTV.setText(tempName);
        viewHolder.descTV.setText(tempDesc);
        viewHolder.upvoteTV.setText(String.valueOf(productList.get(position).upvotes));
        viewHolder.commentsTV.setText(String.valueOf(productList.get(position).productComments));
        Glide.with(context).load(productList.get(position).productThumbnail)
                .into(viewHolder.thumbnailIV);

        return convertView;
    }

    private class ViewHolder {
        TextView nameTV, descTV, upvoteTV, commentsTV;
        ImageView thumbnailIV;
    }
}
