package kz.danke.kids.shop.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("materials")
    private List<Material> materialList;
}
