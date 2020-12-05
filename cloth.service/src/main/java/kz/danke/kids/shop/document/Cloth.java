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

    public Cloth() {
    }

    public Cloth(String id, String name, String description, String gender, List<LineSize> lineSizes,
                 List<String> images, List<Material> materials, String color, Integer price, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.gender = gender;
        this.lineSizes = lineSizes;
        this.images = images;
        this.materials = materials;
        this.color = color;
        this.price = price;
        this.category = category;
    }

    public static ClothBuilder builder() {
        return new ClothBuilder();
    }

    public static class ClothBuilder {
        private String id;
        private String name;
        private String description;
        private String gender;
        private List<LineSize> lineSizes = new ArrayList<>();
        private List<String> images = new ArrayList<>();
        private List<Material> materials = new ArrayList<>();
        private String color;
        private Integer price;
        private String category;

        public ClothBuilder() {
        }

        public ClothBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ClothBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ClothBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ClothBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public ClothBuilder lineSizes(List<LineSize> lineSizes) {
            this.lineSizes = lineSizes;
            return this;
        }

        public ClothBuilder images(List<String> images) {
            this.images = images;
            return this;
        }

        public ClothBuilder materials(List<Material> materials) {
            this.materials = materials;
            return this;
        }

        public ClothBuilder color(String color) {
            this.color = color;
            return this;
        }

        public ClothBuilder category(String category) {
            this.category = category;
            return this;
        }

        public ClothBuilder price(Integer price) {
            this.price = price;
            return this;
        }

        public Cloth build() {
            return new Cloth(id, name, description, gender, lineSizes, images, materials, color, price, category);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<LineSize> getLineSizes() {
        return lineSizes;
    }

    public void setLineSizes(List<LineSize> lineSizes) {
        this.lineSizes = lineSizes;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
