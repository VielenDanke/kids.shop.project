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
    private Integer amount;

    public LineSize(Integer age, String height) {
        this.age = age;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineSize lineSize = (LineSize) o;
        return Objects.equals(age, lineSize.age) &&
                Objects.equals(height, lineSize.height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(age, height);
    }
}
