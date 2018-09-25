package com.example.ssquare.online_food_order.Model;

/**
 * Created by S square on 05-06-2018.
 */

public class Order {
    private String UserPhone;
    private String ProductID;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Discount;
    private String Image;

    public Order()
    {

    }

    public Order(String productID, String productName, String quantity, String price, String discount, String image) {
        ProductID = productID;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Image=image;
    }

    public Order(String userPhone, String productID, String productName, String quantity, String price, String discount, String image) {
        UserPhone = userPhone;
        ProductID = productID;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Image = image;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
