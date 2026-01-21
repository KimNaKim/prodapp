package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    //DB와 직접 연결해서 상호작용하는 코드
    Connection conn = DBConnection.getConnection();

    //Service의 메서드들을 연결
    //1. insert(String name, int price, int qty)
    int insert(String name, int price, int qty){
        String sql = "insert into product(name, price, qty) values (?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, price);
            pstmt.setInt(3, qty);
            pstmt.executeUpdate();

            // 🔹 생성된 id 조회
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // 생성된 id 반환
            }
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    //2. deleteById(int id)
    int deleteById(int id){
        String sql = "delete from product where id = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate(); // 삭제된 행 수
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //3. findById(int id)
    Product findById(int id){
        String sql = "select * from product where id = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getInt("qty")
                );
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    //4. findAll()
    List<Product> findAll(){
        List<Product> list = new ArrayList<>();
        String sql = "select * from product";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getInt("qty")
                );
                list.add(p);
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }
}
