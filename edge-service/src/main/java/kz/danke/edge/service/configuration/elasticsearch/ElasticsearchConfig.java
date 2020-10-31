package kz.danke.edge.service.configuration.elasticsearch;

import kz.danke.edge.service.configuration.AppConfigProperties;
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
@EnableReactiveElasticsearchRepositories(
        basePackages = {"kz.danke.edge.service.repository"},
        repositoryImplementationPostfix = "Repository"
)
public class ElasticsearchConfig extends AbstractReactiveElasticsearchConfiguration {

    private final AppConfigProperties appConfigProperties;

    @Autowired
    public ElasticsearchConfig(AppConfigProperties appConfigProperties) {
        this.appConfigProperties = appConfigProperties;
    }

    @Bean
    @Override
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
