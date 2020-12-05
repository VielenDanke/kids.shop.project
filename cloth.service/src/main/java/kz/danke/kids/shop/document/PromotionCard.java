package kz.danke.kids.shop.document;

import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Objects;

@Document(indexName = "cloth.promotion.card", createIndex = false)
public class PromotionCard {

    private String id;
    private String image;
    private String description;
    private String name;

    public PromotionCard() {
    }

    public PromotionCard(String id, String image, String description, String name) {
        this.id = id;
        this.image = image;
        this.description = description;
        this.name = name;
    }

    public static PromotionCardBuilder builder() {
        return new PromotionCardBuilder();
    }

    public static class PromotionCardBuilder {
        private String id;
        private String image;
        private String description;
        private String name;

        public PromotionCardBuilder() {
        }

        public PromotionCardBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PromotionCardBuilder image(String image) {
            this.image = image;
            return this;
        }

        public PromotionCardBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PromotionCardBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PromotionCard build() {
            return new PromotionCard(id, image, description, name);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PromotionCard that = (PromotionCard) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
