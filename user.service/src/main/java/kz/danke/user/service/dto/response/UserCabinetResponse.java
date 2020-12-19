package kz.danke.user.service.dto.response;

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
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String phoneNumber;

    public static UserCabinetResponse toUserCabinetResponse(User user) {
        return new UserCabinetResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getAddress(),
                user.getCity(),
                user.getPhoneNumber()
        );
    }
}
