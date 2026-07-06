package sev.amorlov.plant_nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sev.amorlov.plant_nursery.model.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}