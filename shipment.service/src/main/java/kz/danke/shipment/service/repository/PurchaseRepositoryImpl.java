package kz.danke.shipment.service.repository;

import kz.danke.shipment.service.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepositoryImpl extends JpaRepository<Purchase, Long> {

    @Query("select p from Purchase p where p.sent=false")
    List<Purchase> findAllBySentStatus();
}
