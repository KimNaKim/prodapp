package dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class RequestDTO {
    //QueryString 클래스 선언하기
    //json 형태를 gpt에게 변환하게 주문해서 작성해보기
    @Setter
    @Getter
    private String method;
    @Setter
    @Getter
    private Map<String, Object> querystring;
    @Setter
    @Getter
    private Map<String, Object> body;

}
