package com.example.thenameless.model;

import java.util.ArrayList;
import java.util.List;

public class ProductType {

    public ProductType(){

    }

    public List<String> getProductTypes(){

        List<String> list = new ArrayList<>();

        list.add("Book");
        list.add("Lab Coat");
        list.add("Instrument");
        list.add("Sports");
        list.add("Other category");

        return (list);
    }
}
