package server;

import java.util.List;

public class ProductService implements ProductServiceInterface {
    private ProductRepository pr;

    public ProductService(){
        this.pr = new ProductRepository();
    }

    @Override
    public int save(String name, int price, int qty) {
        return pr.insert(name, price, qty);
    }

    @Override
    public List<Product> findAll() {
        return pr.findAll();
    }

    @Override
    public Product findById(int id) {
        return pr.findById(id);
    }

    @Override
    public int deleteById(int id) {
        return pr.deleteById(id);
    }
}
