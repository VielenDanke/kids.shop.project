package kz.danke.kids.shop.document;

import lombok.*;

import java.util.Objects;

public class ClothCart {

    private String id;
    private Integer age;
    private String height;
    private Integer amount;
    private Integer price;

    public ClothCart() {
    }

    public ClothCart(String id, Integer age, String height, Integer amount, Integer price) {
        this.id = id;
        this.age = age;
        this.height = height;
        this.amount = amount;
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClothCart clothCart = (ClothCart) o;
        return Objects.equals(id, clothCart.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
