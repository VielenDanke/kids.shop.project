package kz.danke.kids.shop.service;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.repository.CategoryReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.repository.PromotionCardReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.service.impl.CategoryServiceImpl;
import kz.danke.kids.shop.service.impl.ClothServiceImpl;
import kz.danke.kids.shop.service.impl.PromotionServiceImpl;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import kz.danke.kids.shop.service.searching.QueryCreator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PromotionServiceImpl.class, ClothServiceImpl.class, CategoryServiceImpl.class})
@TestExecutionListeners(listeners = {
        MockitoTestExecutionListener.class,
        SpringBootDependencyInjectionTestExecutionListener.class
})
public abstract class AbstractServiceLayer {

    @Autowired
    protected PromotionService promotionService;
    @Autowired
    protected ClothService clothService;
    @Autowired
    protected CategoryService categoryService;

    @MockBean
    protected CategoryReactiveElasticsearchRepositoryImpl categoryRepository;
    @MockBean
    protected ClothReactiveElasticsearchRepositoryImpl clothRepository;
    @MockBean
    protected PromotionCardReactiveElasticsearchRepositoryImpl promotionRepository;
    @MockBean
    protected QueryCreator<Cloth, PublicSearchingObject> queryCreator;
}
