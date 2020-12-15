package kz.danke.kids.shop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClothReactiveElasticsearchRepositoryTest extends AbstractRepositoryLayer {

    @BeforeEach
    public void setup() {
        super.setupElasticsearchTestContainer();
    }

    @Test
    public void testSaveCloth() {

    }
}
