package sev.amorlov.plant_nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sev.amorlov.plant_nursery.model.OrderEntity;
import sev.amorlov.plant_nursery.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    // Ищем все заказы в статусе CREATED, которые были созданы ДО указанного времени
    List<OrderEntity> findAllByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime dateTime);
}