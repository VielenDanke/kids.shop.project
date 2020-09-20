package kz.danke.kids.shop.config;

import kz.danke.kids.shop.exceptions.ElasticsearchIndexPolicyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ElasticsearchCreationListener implements ApplicationListener<ContextRefreshedEvent> {

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
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        final String dot = ".";

        Set<String> indices = appConfigProperties.getElasticsearch()
                .getClassList()
                .stream()
                .map(str -> {
                    for (Package aPackage : packages) {
                        try {
                            return Class.forName(aPackage.getName() + dot + str);
                        } catch (ClassNotFoundException e) {
                            continue;
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
            default:
                throw new ElasticsearchIndexPolicyException("Not valid creation policy");
        }
    }

    private void createIndices(final Set<String> indices) {
        reactiveElasticsearchClient
                .indices()
                .createIndex(createIndexRequest -> {
                    log.info(String.format("Create indices: %s", indices.toString()));

                    indices.forEach(createIndexRequest::index);
                }).block();
    }

    private void deleteIndices(final Set<String> indices) {
        reactiveElasticsearchClient
                .indices()
                .deleteIndex(deleteIndexRequest -> {
                    String[] indicesArray = indices.toArray(String[]::new);

                    log.info(String.format("Delete indices: %s", Arrays.toString(indicesArray)));

                    deleteIndexRequest.indices(indicesArray);
                }).block();
    }

    private void deleteAndCreateIndices(final Set<String> indices) {
        reactiveElasticsearchClient
                .indices()
                .deleteIndex(deleteIndexRequest -> {
                    String[] indicesArray = indices.toArray(String[]::new);

                    log.info(String.format("Delete indices: %s", Arrays.toString(indicesArray)));

                    deleteIndexRequest.indices(indicesArray);
                }).block();
        reactiveElasticsearchClient
                .indices()
                .createIndex(createIndexRequest -> {
                    log.info(String.format("Create indices: %s", indices.toString()));

                    indices.forEach(createIndexRequest::index);
                }).block();
    }
}
