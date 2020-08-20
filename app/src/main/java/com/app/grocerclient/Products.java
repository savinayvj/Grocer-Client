package com.app.grocerclient;

public class Products {
    public String productId;
    public String productName;
    public String productPrice;
    public int productQuantity;
    public String productCategory;
    public String productDescription;

    public Products() {
    }

    // Class to store products
    public Products(String pid, String pname, String pprice, int pquantity, String pcategory, String pdesc) {
        productId = pid;
        productName = pname;
        productPrice = pprice;
        productQuantity = pquantity;
        productCategory = pcategory;
        productDescription = pdesc;


    }
}


