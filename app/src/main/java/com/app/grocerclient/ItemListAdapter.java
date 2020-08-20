package com.app.grocerclient;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.grocerclient.ItemDataModel;
import com.app.grocerclient.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

//Custom Adapter for shopping cart list
public class ItemListAdapter extends ArrayAdapter<ItemDataModel> implements View.OnClickListener{

    private ArrayList<ItemDataModel> dataSet;
    Context mContext;


    // View lookup cache
    private static class ViewHolder {
        ImageView item_image;
        TextView item_name;
        TextView item_price;
        TextView item_quantity;

    }
    public ItemListAdapter(ArrayList<ItemDataModel> data, Context context) {
        super(context, R.layout.item_row, data);
        this.dataSet = data;
        this.mContext=context;



    }
    @Override
    public void onClick(View v) {

    }
    private int lastPosition = -1;
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final ItemDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ItemListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ItemListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_row, parent, false);
            viewHolder.item_name = (TextView) convertView.findViewById(R.id.item_name);
            viewHolder.item_image = (ImageView) convertView.findViewById(R.id.item_image);
            viewHolder.item_price = (TextView) convertView.findViewById(R.id.item_price);
            viewHolder.item_quantity = (TextView) convertView.findViewById(R.id.quantity);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemListAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.item_name.setText(dataModel.getName());
        DecimalFormat formatter = (Locale.getDefault().getLanguage().equals("hi")) ? new DecimalFormat("##,##,###") : new DecimalFormat("#,###,###");
        String price_string = formatter.format(Integer.parseInt(dataModel.getPrice()));
        viewHolder.item_price.setText("Rs " + price_string);
        viewHolder.item_quantity.setText(Integer.toString(dataModel.getQuantity()));




        //Get image for product from Firebase Storage
        new GlideHelper(viewHolder.item_image).getImageForProduct(dataModel.getId());

        return convertView;
    }

}
