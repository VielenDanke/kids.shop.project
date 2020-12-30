package kz.danke.kids.shop.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(createIndex = false, indexName = "cloth.category")
public class Category {

    @Id
    private String id;
    @Field(type = FieldType.Text, includeInParent = true)
    private String category;

    public Category() {
    }

    public Category(String id, String category) {
        this.id = id;
        this.category = category;
    }

    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }

    public static class CategoryBuilder {
        private String id;
        private String category;

        public CategoryBuilder() {
        }

        public CategoryBuilder id(String id) {
            this.id = id;
            return this;
        }

        public CategoryBuilder category(String category) {
            this.category = category;
            return this;
        }

        public Category build() {
            return new Category(id, category);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
