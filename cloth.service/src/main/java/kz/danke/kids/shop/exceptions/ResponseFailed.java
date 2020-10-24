package kz.danke.kids.shop.exceptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseFailed {

    private String description;
    private String type;
}
