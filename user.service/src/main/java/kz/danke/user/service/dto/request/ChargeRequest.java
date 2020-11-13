package kz.danke.user.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRequest {

    private String firstName;
    private String lastName;
    private Integer amount;
    private String currency;
}
