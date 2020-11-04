package kz.danke.kids.shop.service.searching;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicSearchingObject {

    private String name;
    private String description;
    private String material;
    private String sex;
    private Integer age;
    private String height;
    private String color;
}
