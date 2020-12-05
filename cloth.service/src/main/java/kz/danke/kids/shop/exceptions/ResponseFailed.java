package kz.danke.kids.shop.exceptions;

import lombok.*;
import org.springframework.util.StringUtils;

public class ResponseFailed {

    private String description;
    private String type;
    private String path;

    public ResponseFailed() {
    }

    public ResponseFailed(String description, String type, String path) {
        this.description = !StringUtils.isEmpty(description) ? description : "Something went wrong";
        this.type = type;
        this.path = path;
    }

    public static ResponseFailedBuilder builder() {
        return new ResponseFailedBuilder();
    }

    public static class ResponseFailedBuilder {
        private String description;
        private String type;
        private String path;

        public ResponseFailedBuilder() {}

        public ResponseFailedBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ResponseFailedBuilder type(String type) {
            this.type = type;
            return this;
        }

        public ResponseFailedBuilder path(String path) {
            this.path = path;
            return this;
        }

        public ResponseFailed build() {
            return new ResponseFailed(description, type, path);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
