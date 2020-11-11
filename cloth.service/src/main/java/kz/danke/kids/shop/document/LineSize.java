package kz.danke.kids.shop.document;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineSize {

    private Integer age;
    private String height;
    private String color;
    private Integer amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineSize lineSize = (LineSize) o;
        return Objects.equals(age, lineSize.age) &&
                Objects.equals(height, lineSize.height) &&
                Objects.equals(color, lineSize.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(age, height, color);
    }
}
