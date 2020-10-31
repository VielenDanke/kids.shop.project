package kz.danke.edge.service.exception;

import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor
public class ResponseFailed {

    public String type;
    public String description;
    public String path;

    public ResponseFailed(String type, String description, String path) {
        this.type = type;
        this.description = convertIfNotExistsErrorMessage(description);
        this.path = path;
    }

    private String convertIfNotExistsErrorMessage(String message) {
        return !StringUtils.isEmpty(message) ? message : "Something went wrong";
    }
}
