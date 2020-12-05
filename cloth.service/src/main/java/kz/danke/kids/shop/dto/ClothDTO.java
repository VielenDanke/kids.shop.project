package kz.danke.kids.shop.dto;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.document.LineSize;
import kz.danke.kids.shop.document.Material;
import lombok.*;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class ClothDTO {

    private String id;
    private String name;
    private Integer price;
    private String color;
    private List<String> images;
    private String description;
    private List<LineSize> lineSizes;

    public ClothDTO() {
    }

    public ClothDTO(String id, String name, Integer price, String color, List<String> images, String description,
                    List<LineSize> lineSizes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.color = color;
        this.images = images;
        this.description = description;
        this.lineSizes = lineSizes;
    }

    public static ClothDTO toClothDTO(Cloth cloth) {
        return new ClothDTO(
                cloth.getId(),
                cloth.getName(),
                cloth.getPrice(),
                cloth.getColor(),
                cloth.getImages(),
                cloth.getDescription(),
                cloth.getLineSizes()
        );
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LineSize> getLineSizes() {
        return lineSizes;
    }

    public void setLineSizes(List<LineSize> lineSizes) {
        this.lineSizes = lineSizes;
    }
}
