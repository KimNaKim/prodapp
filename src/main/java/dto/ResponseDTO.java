package dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    //제네릭을 사용해야 함
    @Getter
    @Setter
    private String msg;
    @Getter
    @Setter
    private T body;

}
