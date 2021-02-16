package kz.danke.shipment.service.service.impl;

import kz.danke.shipment.service.model.Purchase;
import kz.danke.shipment.service.repository.PurchaseRepositoryImpl;
import kz.danke.shipment.service.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepositoryImpl purchaseRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Purchase save(Purchase purchase) {
        purchase.setBusinessKey(UUID.randomUUID().toString());
        return purchaseRepository.save(purchase);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Purchase> findAllWithChangeStatus() {
        return purchaseRepository.findAllBySentStatus();
    }
}
