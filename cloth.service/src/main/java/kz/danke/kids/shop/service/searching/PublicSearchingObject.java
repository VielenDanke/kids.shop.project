package kz.danke.kids.shop.service.searching;

public class PublicSearchingObject {

    private String name;
    private String description;
    private String material;
    private String gender;
    private Integer age;
    private String height;
    private String color;
    private String category;

    public PublicSearchingObject() {
    }

    public PublicSearchingObject(String name, String description, String material, String gender,
                                 Integer age, String height, String color, String category) {
        this.name = name;
        this.description = description;
        this.material = material;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.color = color;
        this.category = category;
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

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
