package com.app.grocerclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button viewOrderButton = (Button) findViewById(R.id.view_order_button);
        Button searchProductButton = (Button) findViewById(R.id.search_products);
        Button viewStockButton = (Button) findViewById(R.id.view_stock);
        viewOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, OrderListActivity.class);
                startActivity(i);
            }
        });

        Button addbutton = (Button) findViewById(R.id.button1);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("product_templates/");
                myRef.push().setValue(new Products("501106401050","Country Range Frozen Small White Baguettes 28cm - 30x135g","1653",0,"bread",""));
            }
        });

        searchProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ProductSearchActivity.class);
                startActivity(i);
            }
        });

        viewStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,StockActivity.class);
                startActivity(i);
            }
        });


    }
}