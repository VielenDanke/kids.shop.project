package kz.danke.shipment.service.service.impl;

import kz.danke.shipment.service.model.Purchase;
import kz.danke.shipment.service.repository.PurchaseRepositoryImpl;
import kz.danke.shipment.service.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepositoryImpl purchaseEventRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Purchase save(Purchase purchase) {
        return purchaseEventRepository.save(purchase);
    }
}
