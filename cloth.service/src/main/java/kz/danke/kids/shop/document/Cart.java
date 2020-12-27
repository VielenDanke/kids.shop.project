package kz.danke.kids.shop.document;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<ClothCart> clothCartList = new ArrayList<>();

    public Cart() {
    }

    public Cart(List<ClothCart> clothCartList) {
        this.clothCartList = clothCartList;
    }

    public List<ClothCart> getClothCartList() {
        return clothCartList;
    }

    public void setClothCartList(List<ClothCart> clothCartList) {
        this.clothCartList = clothCartList;
    }
}
