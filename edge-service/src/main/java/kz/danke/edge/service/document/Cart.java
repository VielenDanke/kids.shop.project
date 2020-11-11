package kz.danke.edge.service.document;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    List<ClothCart> clothCartSet = new ArrayList<>();
}
