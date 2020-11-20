package kz.danke.kids.shop.document;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "cloth.promotion.card", createIndex = false)
public class PromotionCard {

    private String id;
    private String image;
    private String description;
    private String name;

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
