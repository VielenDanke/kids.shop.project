package kz.danke.user.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppConfigProperties {

    private Elasticsearch elasticsearch = new Elasticsearch();
    private Jwt jwt = new Jwt();

    public static class Jwt {
        private String secret;
        private Long expiration;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Long getExpiration() {
            return expiration;
        }

        public void setExpiration(Long expiration) {
            this.expiration = expiration;
        }
    }

    public static class Elasticsearch {
        private String hostAndPort;
        private String username;
        private String password;
        private String jksStorePath;
        private String jksPassword;

        public String getJksStorePath() {
            return jksStorePath;
        }

        public void setJksStorePath(String jksStorePath) {
            this.jksStorePath = jksStorePath;
        }

        public String getJksPassword() {
            return jksPassword;
        }

        public void setJksPassword(String jksPassword) {
            this.jksPassword = jksPassword;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getHostAndPort() {
            return hostAndPort;
        }

        public void setHostAndPort(String hostAndPort) {
            this.hostAndPort = hostAndPort;
        }
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Elasticsearch getElasticsearch() {
        return elasticsearch;
    }

    public void setElasticsearch(Elasticsearch elasticsearch) {
        this.elasticsearch = elasticsearch;
    }
}
