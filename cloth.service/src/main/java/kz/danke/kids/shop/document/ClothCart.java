package kz.danke.kids.shop.document;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClothCart {

    private String id;
    private Integer age;
    private String height;
    private Integer amount;
    private Integer price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClothCart clothCart = (ClothCart) o;
        return Objects.equals(id, clothCart.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
