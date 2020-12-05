package kz.danke.kids.shop.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Material {

    @JsonProperty("material")
    private String material;
    @JsonProperty("percentage")
    private Integer percentage;

    public Material() {
    }

    public Material(String material, Integer percentage) {
        this.material = material;
        this.percentage = percentage;
    }

    public static MaterialBuilder builder() {
        return new MaterialBuilder();
    }

    public static class MaterialBuilder {
        private String material;
        private Integer percentage;

        public MaterialBuilder() {
        }

        public MaterialBuilder material(String material) {
            this.material = material;
            return this;
        }

        public MaterialBuilder percentage(Integer percentage) {
            this.percentage = percentage;
            return this;
        }

        public Material build() {
            return new Material(material, percentage);
        }
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }
}
