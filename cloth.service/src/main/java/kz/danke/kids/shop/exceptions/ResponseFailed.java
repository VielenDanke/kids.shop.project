package kz.danke.kids.shop.exceptions;

import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ResponseFailed {

    private String description;
    private String type;

    public ResponseFailed(String description, String type) {
        this.description = description;
        this.type = type;
    }

    private String handleDescription(String description) {
        return !StringUtils.isEmpty(description) ? description : "Something went wrong";
    }
}
