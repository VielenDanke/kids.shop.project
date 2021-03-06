package kz.danke.user.service.config.elasticsearch;

import kz.danke.user.service.config.AppConfigProperties;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
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
@EnableReactiveElasticsearchRepositories(basePackages = "kz.danke.edge.kz.danke.user.service.service.kz.danke.user.service.repository")
public class ElasticsearchConfig {

    private final AppConfigProperties appConfigProperties;

    @Autowired
    public ElasticsearchConfig(AppConfigProperties appConfigProperties) {
        this.appConfigProperties = appConfigProperties;
    }

    @Bean("reactiveElasticsearchTemplate")
    public ReactiveElasticsearchOperations reactiveElasticsearchOperations(
            ReactiveElasticsearchClient reactiveElasticsearchClient,
            @Qualifier("mappingElasticsearchConverter") ElasticsearchConverter elasticsearchConverter
    ) {
        return new ReactiveElasticsearchTemplate(reactiveElasticsearchClient, elasticsearchConverter);
    }

    @Bean("mappingElasticsearchConverter")
    public ElasticsearchConverter mappingElasticsearchConverter(
            @Qualifier("mappingContext") SimpleElasticsearchMappingContext mappingContext) {
        return new MappingElasticsearchConverter(mappingContext);
    }

    @Bean("mappingContext")
    public SimpleElasticsearchMappingContext mappingContext() {
        return new SimpleElasticsearchMappingContext();
    }

    @Bean("reactiveElasticsearchClient")
    public ReactiveElasticsearchClient reactiveElasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(appConfigProperties.getElasticsearch().getHostAndPort())
//                .usingSsl(Objects.requireNonNull(generateSslContext()))
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

    private SSLContext generateSslContext() {
        try {
            return SSLContextBuilder
                    .create()
                    .loadTrustMaterial(new File(
                                    appConfigProperties.getElasticsearch().getJksStorePath()),
                            appConfigProperties.getElasticsearch().getJksPassword().toCharArray()
                    )
                    .build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
