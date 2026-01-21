package server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

class ProductRepositoryTest {
    private ProductRepository repo;

    @BeforeEach
    void setUp() {
        repo = new ProductRepository();
    }

    @Test
    void findById_test() {
        int id = 5;

        Product product = repo.findById(id);

        Assertions.assertEquals(id, product.getId());
    }

    @Test
    void insert_test() {
        String name = "딸기";
        int price = 1000;
        int qty = 5;

        int generatedId = repo.insert(name, price, qty);

        Assertions.assertTrue(generatedId > 0);
    }

    @Test
    void delete_test() {
        int id = 8;

        int result = repo.deleteById(id);

        Assertions.assertEquals(1, result);
    }

    @Test
    void findAll_test() {
        List<Product> products = repo.findAll();

        Assertions.assertNotNull(products);
        Assertions.assertFalse(products.isEmpty());
    }
}
