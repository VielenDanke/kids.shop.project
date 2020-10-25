package kz.danke.kids.shop.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Material {

    @JsonProperty("material")
    private String material;
    @JsonProperty("percentage")
    private Integer percentage;
}
