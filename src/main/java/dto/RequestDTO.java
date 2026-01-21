package dto;

import lombok.*;
import server.Product;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTO {
    //QueryString 클래스 선언하기
    //json 형태를 gpt에게 변환하게 주문해서 작성해보기
    @Setter
    @Getter
    private String method;
    @Setter
    @Getter
    private Map<String, Integer> querystring;
    @Setter
    @Getter
    private Product product;

}
