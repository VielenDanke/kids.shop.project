package kz.danke.kids.shop.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Objects;

@Document(indexName = "kids_shop_cloth")
public class Cloth {

    @Id
    private String id;
    @Field(type = FieldType.Text, includeInParent = true)
    private String description;

    public Cloth() {
    }

    public Cloth(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public static ClothBuilder builder() {
        return new ClothBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class ClothBuilder {
        private String id;
        private String description;

        ClothBuilder() {
        }

        public ClothBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ClothBuilder description(String description) {
            this.description = description;
            return this;
        }

        public Cloth build() {
            return new Cloth(id, description);
        }
    }

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
