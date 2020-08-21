package com.app.grocerclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ItemDetailsActivity extends AppCompatActivity {

    TextView item_name_detailed;
    ImageView item_image_detailed;
    EditText item_price_detailed;
    ImageView item_add,item_remove;
    TextView item_count;
    Button addToStock;
    TextView item_desc;
    String update_key;
    Spinner categories;
    Button removeFromStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        item_name_detailed = (TextView)findViewById(R.id.item_name_detailed);
        item_image_detailed = (ImageView) findViewById(R.id.item_image_detailed);
        item_price_detailed = (EditText) findViewById(R.id.item_price_detailed);
        item_add = (ImageView) findViewById(R.id.item_add);
        item_remove = (ImageView) findViewById(R.id.item_delete);
        item_count = (TextView) findViewById(R.id.item_count);
        addToStock = (Button) findViewById(R.id.addtostock_button);
        item_desc = (TextView)findViewById(R.id.item_desc_detailed);
        categories = (Spinner) findViewById(R.id.category_selector);
        removeFromStock = (Button) findViewById(R.id.removefromstock_button);
        final String[] categories_list = getResources().getStringArray(R.array.categories);


        Bundle extras = getIntent().getExtras();
        final String id = extras.getString("id");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("product_templates");
        Query qr = myRef.orderByChild("productId").equalTo(id);

        qr.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren() ){

                    Products prod1 = data.getValue(Products.class);
                    update_key = data.getKey().toString();
                    item_name_detailed.setText(prod1.productName);
                    //DecimalFormat formatter = (Locale.getDefault().getLanguage().equals("hi")) ? new DecimalFormat("##,##,###") : new DecimalFormat("#,###,###");
                    //String price_string = formatter.format(Integer.parseInt(prod1.productPrice));
                    item_price_detailed.setText(prod1.productPrice);
                    item_desc.setText(prod1.productDescription);
                    categories.setSelection(Arrays.asList(categories_list).indexOf(prod1.productCategory));


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // add quantity button (+)
        item_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int count = Integer.valueOf(item_count.getText().toString()) +1;
                item_count.setText(Integer.toString(count));

            }



        });

        // remove quantity button (-)
        item_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.valueOf(item_count.getText().toString()) -1;
                if(count>=0) {
                    item_count.setText(Integer.toString(count));
                }
            }
        });

        addToStock.setOnClickListener(new View.OnClickListener() {
            int new_quantity;
            @Override
            public void onClick(View view) {

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("products");
                Query qr = myRef.orderByChild("productId").equalTo(id);
                qr.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {

                                Products prod1 = data.getValue(Products.class);
                                new_quantity = prod1.productQuantity + Integer.parseInt(item_count.getText().toString());
                                finish();


                            }

                        }else{
                            new_quantity = Integer.parseInt(item_count.getText().toString());
                        }
                        DatabaseReference update_ref = database.getReference("products/" + update_key);
                        update_ref.setValue(new Products(id, item_name_detailed.getText().toString(), item_price_detailed.getText().toString(), new_quantity, categories.getSelectedItem().toString(), item_desc.getText().toString()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });


        removeFromStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("products");
                Query qr = myRef.orderByChild("productId").equalTo(id);
                qr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot :  snapshot.getChildren())
                        {
                            dataSnapshot.getRef().setValue(null);
                            finish();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        //Get image for product from Firebase Storage
        new GlideHelper(item_image_detailed).getImageForProduct(id);


    }
}