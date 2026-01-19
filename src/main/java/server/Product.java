package server;

import lombok.Getter;
import lombok.Setter;

public class Product {
    private int id;
    private String name;
    private int price;
    private int qty;

    public Product(int id, String name, int price, int qty) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.price = price;
    }
}
