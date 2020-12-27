package kz.danke.kids.shop.dto.request;

public class CategorySaveRequest {

    private String categoryName;

    public CategorySaveRequest() {
    }

    public CategorySaveRequest(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
