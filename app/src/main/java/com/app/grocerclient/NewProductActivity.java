package com.app.grocerclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NewProductActivity extends AppCompatActivity {
    private int PICK_IMAGE_REQUEST = 1;
    ImageView imageView;
    EditText product_id,product_name,product_desc,product_price;
    TextView product_quantity;
    Spinner category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        imageView = (ImageView) findViewById(R.id.produt_image);
        Button upload = (Button) findViewById(R.id.upload);
        product_id = (EditText) findViewById(R.id.product_id);
        product_name = (EditText) findViewById(R.id.product_name);
        product_desc = (EditText) findViewById(R.id.product_description);
        product_price = (EditText) findViewById(R.id.product_price);
        product_quantity = (TextView) findViewById(R.id.item_count);
        category = (Spinner) findViewById(R.id.category_selector);
        Button save = (Button) findViewById(R.id.addtostock_button);
        ImageView item_add = (ImageView) findViewById(R.id.item_add);
        ImageView item_remove = (ImageView) findViewById(R.id.item_delete);



        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            chooseImage();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = product_id.getText().toString();
                String name = product_name.getText().toString();
                String desc = product_desc.getText().toString();
                String price = product_price.getText().toString();
                int quantity = Integer.parseInt(product_quantity.getText().toString());
                String pcategory = category.getSelectedItem().toString();
                addToTemplate(id,name,price,pcategory,desc);
                addToStock(id,name,price,quantity,pcategory,desc);
                addToSearch(id);
                uploadImage(id);
                Toast.makeText(NewProductActivity.this,"Product added Sucesfully", Toast.LENGTH_SHORT).show();
            }
        });

        // add quantity button (+)
        item_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int count = Integer.valueOf(product_quantity.getText().toString()) +1;
                product_quantity.setText(Integer.toString(count));

            }



        });

        // remove quantity button (-)
        item_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.valueOf(product_quantity.getText().toString()) -1;
                if(count>=0) {
                    product_quantity.setText(Integer.toString(count));
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,300,300,false);
                // Log.d(TAG, String.valueOf(bitmap));
                imageView.setImageBitmap(scaledBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    public void uploadImage(String id){
        // Get the data from an ImageView as bytes
        FirebaseStorage storage = FirebaseStorage.getInstance();;
        StorageReference storageRef = storage.getReference().child("productImages/" + id + ".jpg");
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                finish();
            }
        });

    }

    public void addToTemplate(String pid,String pname,String pprice,String pcategory,String pdesc){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("product_templates/");
        myRef.push().setValue(new Products(pid,pname,pprice,0,pcategory,pdesc));
    }

    public void addToStock(String pid,String pname,String pprice,int pquantity, String pcategory,String pdesc){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products/");
        myRef.push().setValue(new Products(pid,pname,pprice,pquantity,pcategory,pdesc));
    }

    public void addToSearch(String id){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("search_product_templates/");
        myRef.push().setValue(id);

    }
}