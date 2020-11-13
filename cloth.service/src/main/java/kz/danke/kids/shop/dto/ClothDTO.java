package kz.danke.kids.shop.dto;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.document.Material;
import lombok.*;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClothDTO {

    private String id;
    private String name;
    private Integer price;
    private String color;
    private List<String> images;

    public static ClothDTO toClothDTO(Cloth cloth) {
        return new ClothDTO(
                cloth.getId(),
                cloth.getName(),
                cloth.getPrice(),
                cloth.getColor(),
                cloth.getImages()
        );
    }
}
