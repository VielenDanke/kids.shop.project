package kz.danke.kids.shop.document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineSize {

    private Size size;
    private ColorAmount colorAmount;
}
