package server;

import java.util.List;

public interface ProductServiceInterface {
    //상품등록,상품목록, 상품상세, 상품삭제
    //public abstract를 생략 가능
    int save(String name, int price, int qty);
    List<Product> findAll();
    Product findById(int id);
    int deleteById(int id);

}
