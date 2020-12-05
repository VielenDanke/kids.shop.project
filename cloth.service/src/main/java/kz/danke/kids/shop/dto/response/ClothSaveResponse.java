package kz.danke.kids.shop.dto.response;

public class ClothSaveResponse {

    private String id;
    private String name;
    private String description;

    public ClothSaveResponse() {
    }

    public ClothSaveResponse(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static ClothSaveResponseBuilder builder() {
        return new ClothSaveResponseBuilder();
    }

    public static class ClothSaveResponseBuilder {
        private String id;
        private String name;
        private String description;

        public ClothSaveResponseBuilder() {
        }

        public ClothSaveResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ClothSaveResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ClothSaveResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ClothSaveResponse build() {
            return new ClothSaveResponse(id, name, description);
        }
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
