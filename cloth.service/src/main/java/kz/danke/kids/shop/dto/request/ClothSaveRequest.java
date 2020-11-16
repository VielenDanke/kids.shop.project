package kz.danke.kids.shop.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import kz.danke.kids.shop.document.LineSize;
import kz.danke.kids.shop.document.Material;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClothSaveRequest {

    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("sex")
    private String sex;
    @JsonProperty("color")
    private String color;
    @JsonProperty("price")
    private Integer price;
    @JsonProperty("category")
    private String category;
    @JsonProperty("lineSizes")
    private List<LineSize> lineSizes;
    @JsonProperty("materials")
    private List<Material> materialList;
}
