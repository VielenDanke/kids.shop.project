package kz.danke.kids.shop.document;

import lombok.*;

import java.util.Objects;

public class LineSize {

    private Integer age;
    private String height;
    private Integer amount;

    public LineSize() {
    }

    public LineSize(Integer age, String height) {
        this.age = age;
        this.height = height;
    }

    public LineSize(Integer age, String height, Integer amount) {
        this.age = age;
        this.height = height;
        this.amount = amount;
    }

    public static LineSizeBuilder builder() {
        return new LineSizeBuilder();
    }

    public static class LineSizeBuilder {
        private Integer age;
        private String height;
        private Integer amount;

        public LineSizeBuilder() {
        }

        public LineSizeBuilder age(Integer age) {
            this.age = age;
            return this;
        }

        public LineSizeBuilder height(String height) {
            this.height = height;
            return this;
        }

        public LineSizeBuilder amount(Integer amount) {
            this.amount = amount;
            return this;
        }

        public LineSize build() {
            return new LineSize(age, height, amount);
        }
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
        LineSize lineSize = (LineSize) o;
        return Objects.equals(age, lineSize.age) &&
                Objects.equals(height, lineSize.height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(age, height);
    }

    @Override
    public String toString() {
        return "LineSize{" +
                "age=" + age +
                ", height='" + height + '\'' +
                ", amount=" + amount +
                '}';
    }
}
