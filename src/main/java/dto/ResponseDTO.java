package dto;

import lombok.Getter;
import lombok.Setter;

public class ResponseDTO<T> {
    //제네릭을 사용해야 함
    @Getter
    @Setter
    private String msg;
    @Getter
    @Setter
    private T body;

    public ResponseDTO() {
        this.msg = "";
        this.body = null;
    }
}
