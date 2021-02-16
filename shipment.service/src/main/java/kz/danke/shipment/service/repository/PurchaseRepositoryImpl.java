package kz.danke.shipment.service.repository;

import kz.danke.shipment.service.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepositoryImpl extends JpaRepository<Purchase, Long> {
}
