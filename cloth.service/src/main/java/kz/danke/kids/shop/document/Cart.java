package kz.danke.kids.shop.document;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    List<ClothCart> clothCartList = new ArrayList<>();
}
