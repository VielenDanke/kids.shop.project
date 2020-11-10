package kz.danke.kids.shop.document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorAmount {

    private String color;
    private Integer amount;
}
