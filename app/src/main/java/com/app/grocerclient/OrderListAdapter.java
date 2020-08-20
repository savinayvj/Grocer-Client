package com.app.grocerclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderListAdapter extends ArrayAdapter<Orders> implements View.OnClickListener {

    private ArrayList<Orders> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView orderNumber;
        TextView orderAddress;
        TextView orderPhone;
        TextView orderName;
        CheckBox checkBox;
        RelativeLayout orderBackground;
    }

    public OrderListAdapter(ArrayList<Orders> data, Context context) {
        super(context, R.layout.order_list_layout, data);
        this.dataSet = data;
        this.mContext=context;

    }
    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Orders dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.order_list_layout, parent, false);
            viewHolder.orderNumber = (TextView) convertView.findViewById(R.id.order_number);
            viewHolder.orderName = (TextView) convertView.findViewById(R.id.order_name);
            viewHolder.orderPhone = (TextView) convertView.findViewById(R.id.order_phnumber);
            viewHolder.orderAddress = (TextView) convertView.findViewById(R.id.order_address);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            viewHolder.orderBackground = (RelativeLayout) convertView.findViewById(R.id.order_background);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        final Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        // set Item's Name
        viewHolder.orderNumber.setText("Order #" + dataModel.id);
        viewHolder.orderName.setText(dataModel.name);
        viewHolder.orderPhone.setText(dataModel.phone);
        viewHolder.orderAddress.setText(dataModel.address);
        final Drawable default_background = viewHolder.orderBackground.getBackground();
        final View finalConvertView = convertView;
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(viewHolder.checkBox.isChecked()){
                    AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Do you want to complete the order?") .setTitle("Alert")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    final DatabaseReference myRef = database.getReference("orders");
                                    Query qr = myRef.orderByChild("id").equalTo(dataModel.id);
                                    qr.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dataSnapshot :  snapshot.getChildren())
                                            {
                                                dataSnapshot.getRef().setValue(null);
                                                remove(getItem(position));

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    viewHolder.checkBox.setChecked(false);
                                    dialogInterface.cancel();
                                }
                            });

                    notifyDataSetChanged();

                    AlertDialog alert = builder.create();
                    alert.show();

                }


            }
        });

        viewHolder.orderNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext,OrderDetailsActivity.class);
                i.putExtra("id",dataModel.id);
                mContext.startActivity(i);
            }
        });




        return convertView;


    }


    @Override
    public void onClick(View view) {

    }
}
