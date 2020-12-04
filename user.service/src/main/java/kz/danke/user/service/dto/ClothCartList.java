package kz.danke.user.service.dto;

import kz.danke.user.service.document.ClothCart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClothCartList {

    private List<ClothCart> clothCartList;
}
