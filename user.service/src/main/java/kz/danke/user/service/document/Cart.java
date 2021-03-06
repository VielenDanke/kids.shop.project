package kz.danke.user.service.document;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    private List<ClothCart> clothCartList = new ArrayList<>();
}
