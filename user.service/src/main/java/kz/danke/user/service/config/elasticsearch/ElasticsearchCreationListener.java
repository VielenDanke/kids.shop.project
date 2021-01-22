package kz.danke.user.service.config.elasticsearch;

import kz.danke.user.service.config.AppConfigProperties;
import kz.danke.user.service.exception.ElasticsearchIndexPolicyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ElasticsearchCreationListener implements ApplicationListener<ApplicationStartedEvent> {

    private final AppConfigProperties appConfigProperties;
    private final Package[] packages = Package.getPackages();
    private final ReactiveElasticsearchClient reactiveElasticsearchClient;

    @Autowired
    public ElasticsearchCreationListener(AppConfigProperties appConfigProperties,
                                         ReactiveElasticsearchClient reactiveElasticsearchClient) {
        this.appConfigProperties = appConfigProperties;
        this.reactiveElasticsearchClient = reactiveElasticsearchClient;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        final String dot = ".";

        Set<String> indices = appConfigProperties.getElasticsearch()
                .getClassList()
                .stream()
                .map(str -> {
                    for (Package aPackage : packages) {
                        if (aPackage.getName().equals(appConfigProperties.getElasticsearch().getBasePackage())) {
                            try {
                                return Class.forName(aPackage.getName() + dot + str);
                            } catch (ClassNotFoundException e) {
                                continue;
                            }
                        }
                    }
                    return null;
                })
                .filter(cls -> cls != null && cls.isAnnotationPresent(Document.class))
                .map(cls -> cls.getAnnotation(Document.class).indexName())
                .collect(Collectors.toSet());

        ElasticsearchCreationPolicy creationPolicy = appConfigProperties.getElasticsearch().getCreationPolicy();

        switch (creationPolicy) {
            case DROP_CREATE:
                deleteAndCreateIndices(indices);
                break;
            case NONE:
                log.info(String.format("Indices creation policy declared as %s", creationPolicy.name()));
                break;
            case DROP:
                deleteIndices(indices);
                break;
            case CREATE:
                createIndices(indices);
                break;
            case UPDATE:
                updateIndices(indices);
                break;
            default:
                throw new ElasticsearchIndexPolicyException("Not valid creation policy");
        }
    }

    private void updateIndices(Set<String> indices) {
        Flux.fromIterable(indices)
                .filter(index -> reactiveElasticsearchClient.indices().existsIndex(getIndexRequest ->
                        getIndexRequest.indices(index)).blockOptional().orElse(false))
                .flatMap(index -> reactiveElasticsearchClient.indices().updateMapping(putMappingRequest ->
                        putMappingRequest.indices(index)))
                .blockLast();
    }

    private void createIndices(final Set<String> indices) {
        Flux.fromIterable(indices)
                .filter(index -> {
                    Optional<Boolean> existOptional = reactiveElasticsearchClient.indices().existsIndex(getIndexRequest ->
                            getIndexRequest.indices(index)).blockOptional();
                    return existOptional.map(aBoolean -> !aBoolean).orElse(true);
                })
                .doOnNext(index -> log.info(String.format("Index is creating: %s", index)))
                .flatMap(index -> reactiveElasticsearchClient.indices().createIndex(createIndexRequest ->
                        createIndexRequest.index(index)))
                .blockLast();
    }

    private void deleteIndices(final Set<String> indices) {
        Flux.fromIterable(indices)
                .filter(index -> reactiveElasticsearchClient.indices().existsIndex(getIndexRequest ->
                        getIndexRequest.indices(index)).blockOptional().orElse(false))
                .doOnNext(index -> log.info(String.format("Index is deleting: %s", index)))
                .flatMap(index -> reactiveElasticsearchClient.indices().deleteIndex(deleteIndexRequest ->
                        deleteIndexRequest.indices(index)))
                .blockLast();
    }

    private void deleteAndCreateIndices(final Set<String> indices) {
        this.deleteIndices(indices);
        this.createIndices(indices);
    }
}
