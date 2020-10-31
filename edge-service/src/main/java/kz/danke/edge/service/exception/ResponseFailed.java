package kz.danke.edge.service.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ResponseFailed {

    public String type;
    public String description;
}
