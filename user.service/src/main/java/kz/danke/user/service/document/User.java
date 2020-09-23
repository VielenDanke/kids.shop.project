package kz.danke.user.service.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Document(indexName = "cloth.shop.user")
public class User {

    @Id
    private String id;
    @Field(type = FieldType.Text)
    private String username;
    @Field(type = FieldType.Text)
    private String password;
    @Field(type = FieldType.Text)
    private Set<String> authorities;

    public User() {
    }

    private User(String id, String username, String password, Set<String> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public static class UserBuilder {
        private String id;
        private String username;
        private String password;
        private Set<String> authorities;

        private UserBuilder() {
        }

        public UserBuilder id(String id) {
            this.id = id;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder authorities(Collection<String> authorities) {
            if (this.authorities == null) {
                this.authorities = new HashSet<>();
            }
            this.authorities.addAll(authorities);
            return this;
        }

        public User build() {
            return new User(id, username, password, authorities);
        }
    }
}
