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
    private String path;

    public ResponseFailed(String description, String type, String path) {
        this.description = !StringUtils.isEmpty(description) ? description : "Something went wrong";
        this.type = type;
        this.path = path;
    }
}
