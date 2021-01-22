package kz.danke.user.service.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
        return Objects.equals(id, clothCart.id) && Objects.equals(age, clothCart.age) && Objects.equals(height, clothCart.height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, age, height);
    }
}
