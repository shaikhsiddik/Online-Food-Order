package com.example.ssquare.online_food_order.Model;

/**
 * Created by S square on 04-06-2018.
 */

public class Food
{
    private String Name,Image,Description,Price,MenuId;
    private String Discount;

    public Food()
    {

    }

    public Food(String name, String image, String description, String price,  String menuId,String discount) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount=discount;
        MenuId = menuId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }


    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
