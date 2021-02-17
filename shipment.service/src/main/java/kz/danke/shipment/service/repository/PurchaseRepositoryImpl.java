package kz.danke.shipment.service.repository;

import kz.danke.shipment.service.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepositoryImpl extends JpaRepository<Purchase, Long> {

    @Query("select p from Purchase p where p.sent=false")
    List<Purchase> findAllBySentStatus();

    @Modifying
    @Query("update Purchase p set p.sent=true where p.id in (?1)")
    void updateStatusByIdIn(List<Long> ids);
}
