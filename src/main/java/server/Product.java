package server;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Setter
    @Getter
    private Integer id;
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private Integer price;
    @Setter
    @Getter
    private Integer qty;

}
