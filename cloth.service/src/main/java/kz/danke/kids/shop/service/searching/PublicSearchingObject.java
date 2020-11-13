package kz.danke.kids.shop.service.searching;

import lombok.*;

@Getter
@Setter
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
