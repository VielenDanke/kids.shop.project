package kz.danke.shipment.service.service;

import kz.danke.shipment.service.model.Purchase;

import java.util.List;

public interface PurchaseService {

    Purchase save(Purchase purchase);

    List<Purchase> findAll();

    List<Purchase> findAllWithChangeStatus();

    void updateStatusByIds(List<Long> ids);
}
