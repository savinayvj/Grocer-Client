package com.app.grocerclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        final ListView productsList = (ListView) findViewById(R.id.products_list);
        final ArrayList<ItemDataModel> dataModel = new ArrayList<>();
        final TextView name = (TextView) findViewById(R.id.name);
        TextView id_text = (TextView) findViewById(R.id.order_id) ;
        final TextView address = (TextView) findViewById(R.id.address);
        final TextView phone = (TextView) findViewById(R.id.phone);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("orders");
        Bundle extras = getIntent().getExtras();
        int id = extras.getInt("id");
        id_text.setText("Order #" + id);
        Query qr = myRef.orderByChild("id").equalTo(id);
        qr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Orders temp = data.getValue(Orders.class);
                        name.setText(temp.name);
                        address.setText(temp.address);
                        phone.setText(temp.phone);
                        ArrayList<Products> prods = temp.products;
                        ArrayList<Integer> prodsquantity = temp.quantities;
                        for(int i=0;i<prods.size();i++) {
                            dataModel.add(new ItemDataModel(prods.get(i).productName, prods.get(i).productPrice, prods.get(i).productId, prodsquantity.get(i)));
                        }
                    }
                }
                ItemListAdapter adapter = new ItemListAdapter(dataModel,OrderDetailsActivity.this);
                productsList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}