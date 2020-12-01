package kz.danke.kids.shop.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(indexName = "kids_shop_cloth", createIndex = false)
@TypeAlias("clothes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cloth {

    @Id
    private String id;
    @Field(type = FieldType.Text, includeInParent = true)
    private String name;
    @Field(type = FieldType.Text, includeInParent = true)
    private String description;
    @Field(type = FieldType.Text, includeInParent = true)
    private String gender;
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<LineSize> lineSizes = new ArrayList<>();
    @Field(type = FieldType.Text, includeInParent = true)
    private List<String> images = new ArrayList<>();
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Material> materials = new ArrayList<>();
    @Field(type = FieldType.Text, includeInParent = true)
    private String color;
    @Field(type = FieldType.Integer, includeInParent = true)
    private Integer price;
    @Field(type = FieldType.Text, includeInParent = true)
    private String category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cloth cloth = (Cloth) o;
        return Objects.equals(id, cloth.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
