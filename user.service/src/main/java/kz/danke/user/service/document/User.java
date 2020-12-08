package kz.danke.user.service.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Objects;
import java.util.Set;

@Document(indexName = "cloth.shop.user", createIndex = false)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;
    @Field(type = FieldType.Text)
    private String username;
    @Field(type = FieldType.Text)
    private String password;
    @Field(type = FieldType.Text)
    private String firsName;
    @Field(type = FieldType.Text)
    private String lastName;
    @Field(type = FieldType.Text)
    private String address;
    @Field(type = FieldType.Text)
    private String city;
    @Field(type = FieldType.Text)
    private String phoneNumber;
    @Field(type = FieldType.Text)
    private Set<String> authorities;
    @Field(type = FieldType.Nested, includeInParent = true)
    private Cart cart;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
