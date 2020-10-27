package kz.danke.kids.shop.dto;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.document.Material;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClothDTO {

    private String id;
    private String name;
    private String description;
    private List<Material> materials;
    private List<byte[]> images;

    public static ClothDTO toClothDTO(Cloth cloth) {
        List<byte[]> images = cloth
                .getImages()
                .parallelStream()
                .map(str -> Base64.getDecoder().decode(str))
                .collect(Collectors.toList());

        return new ClothDTO(cloth.getId(), cloth.getName(), cloth.getDescription(), cloth.getMaterials(), images);
    }
}
