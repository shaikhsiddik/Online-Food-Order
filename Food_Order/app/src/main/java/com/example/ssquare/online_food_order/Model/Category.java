package com.example.ssquare.online_food_order.Model;

/**
 * Created by S square on 04-06-2018.
 */

public class Category
{
    private String Name;
    private String Image;


    Category()
    {

    }

    public Category(String name, String image) {
        Name = name;
        Image = image;

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


}
