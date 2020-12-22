package kz.danke.kids.shop.util;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

public class PartImpl implements Part {

    private static final DataBufferFactory FACTORY = new DefaultDataBufferFactory();

    private final String fileName;
    private final Resource resource;

    public PartImpl(String fileName) {
        this.fileName = fileName;
        this.resource = new ByteArrayResource(fileName.getBytes(StandardCharsets.UTF_8));
    }

    public PartImpl(String fileName, Resource resource) {
        this.fileName = fileName;
        this.resource = resource;
    }

    @Override
    public String name() {
        return fileName;
    }

    @Override
    public HttpHeaders headers() {
        return HttpHeaders.EMPTY;
    }

    @Override
    public Flux<DataBuffer> content() {
        return DataBufferUtils.read(resource, FACTORY, 1024);
    }
}
