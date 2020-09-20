package kz.danke.kids.shop.config;

import lombok.SneakyThrows;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Configuration
@EnableReactiveElasticsearchRepositories
public class ElasticsearchConfig extends AbstractReactiveElasticsearchConfiguration {

    private final AppConfigProperties appConfigProperties;

    @Autowired
    public ElasticsearchConfig(AppConfigProperties appConfigProperties) {
        this.appConfigProperties = appConfigProperties;
    }

    @SneakyThrows
    @Bean
    @Override
    public ReactiveElasticsearchClient reactiveElasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(appConfigProperties.getElasticsearch().getHostAndPort())
                .usingSsl(generateSslContext())
                .withBasicAuth(
                        appConfigProperties.getElasticsearch().getUsername(),
                        appConfigProperties.getElasticsearch().getPassword()
                )
                .withWebClientConfigurer(webClient -> {
                    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                            .build();

                    return webClient.mutate().exchangeStrategies(exchangeStrategies).build();
                })
                .build();

        return ReactiveRestClients.create(clientConfiguration);
    }

    private SSLContext generateSslContext() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        return SSLContextBuilder
                .create()
                .loadTrustMaterial(new File(
                        appConfigProperties.getElasticsearch().getJksStorePath()),
                        appConfigProperties.getElasticsearch().getJksPassword().toCharArray()
                )
                .build();
    }
}
