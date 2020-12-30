package kz.danke.kids.shop.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import kz.danke.kids.shop.document.LineSize;
import kz.danke.kids.shop.document.Material;

import java.util.List;

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

    public ClothSaveRequest() {
    }

    public ClothSaveRequest(String name, String description, String sex, String color, Integer price,
                            String category, List<LineSize> lineSizes, List<Material> materialList) {
        this.name = name;
        this.description = description;
        this.sex = sex;
        this.color = color;
        this.price = price;
        this.category = category;
        this.lineSizes = lineSizes;
        this.materialList = materialList;
    }

    public static ClothSaveRequestBuilder builder() {
        return new ClothSaveRequestBuilder();
    }

    public static class ClothSaveRequestBuilder {

        private String name;
        private String description;
        private String sex;
        private String color;
        private Integer price;
        private String category;
        private List<LineSize> lineSizes;
        private List<Material> materialList;

        public ClothSaveRequestBuilder() {
        }

        public ClothSaveRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ClothSaveRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ClothSaveRequestBuilder sex(String sex) {
            this.sex = sex;
            return this;
        }

        public ClothSaveRequestBuilder color(String color) {
            this.color = color;
            return this;
        }

        public ClothSaveRequestBuilder price(Integer price) {
            this.price = price;
            return this;
        }

        public ClothSaveRequestBuilder category(String category) {
            this.category = category;
            return this;
        }

        public ClothSaveRequestBuilder lineSizes(List<LineSize> lineSizes) {
            this.lineSizes = lineSizes;
            return this;
        }

        public ClothSaveRequestBuilder materialList(List<Material> materialList) {
            this.materialList = materialList;
            return this;
        }

        public ClothSaveRequest build() {
            return new ClothSaveRequest(name, description, sex, color, price, category, lineSizes, materialList);
        }
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public List<LineSize> getLineSizes() {
        return lineSizes;
    }

    public void setLineSizes(List<LineSize> lineSizes) {
        this.lineSizes = lineSizes;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<Material> materialList) {
        this.materialList = materialList;
    }
}
