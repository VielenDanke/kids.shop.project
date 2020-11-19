package kz.danke.user.service.dto.response;

import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCabinetResponse {

    private String id;
    private String username;
    private String firsName;
    private String surname;
    private String address;
    private String phoneNumber;
    private Cart cart;

    public static UserCabinetResponse toUserCabinetResponse(User user) {
        return new UserCabinetResponse(
                user.getId(),
                user.getUsername(),
                user.getFirsName(),
                user.getSurname(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getCart()
        );
    }
}
