package sev.amorlov.plant_nursery.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sev.amorlov.plant_nursery.dto.*;
import sev.amorlov.plant_nursery.event.OrderCreatedEvent;
import sev.amorlov.plant_nursery.exception.InsufficientStockException;
import sev.amorlov.plant_nursery.model.*;
import sev.amorlov.plant_nursery.repository.OrderRepository;
import sev.amorlov.plant_nursery.repository.PlantRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final PlantRepository plantRepository;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(value = {"plants", "plant"}, allEntries = true) // Сбрасываем кэш, так как остатки изменятся
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        log.info("Starting order creation for customer: {}", dto.customerEmail());

        OrderEntity order = new OrderEntity();
        order.setCustomerEmail(dto.customerEmail());
        order.setStatus(OrderStatus.CREATED);

        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : dto.items()) {
            PlantEntity plant = plantRepository.findByIdForUpdate(itemDto.plantId())
                    .orElseThrow(() -> new IllegalArgumentException("Plant not found with id: " + itemDto.plantId()));

            if (plant.getQuantity() < itemDto.quantity()) {
                throw new InsufficientStockException(String.format(
                        "Недостаточно товара '%s' на складе. Запрошено: %d, в наличии: %d",
                        plant.getName(), itemDto.quantity(), plant.getQuantity()
                ));
            }

            plant.setQuantity(plant.getQuantity() - itemDto.quantity());
            plantRepository.save(plant);

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setPlant(plant);
            orderItem.setQuantity(itemDto.quantity());
            orderItem.setPriceAtPurchase(plant.getPrice());

            BigDecimal itemTotalPrice = plant.getPrice().multiply(BigDecimal.valueOf(itemDto.quantity()));
            totalOrderPrice = totalOrderPrice.add(itemTotalPrice);

            order.addItem(orderItem);
        }

        order.setTotalPrice(totalOrderPrice);
        OrderEntity savedOrder = orderRepository.save(order);

        eventPublisher.publishEvent(new OrderCreatedEvent(this, savedOrder.getId(), dto.customerEmail()));

        log.info("Order successfully created with ID: {}, Total Price: {}", savedOrder.getId(), savedOrder.getTotalPrice());
        return orderMapper.toResponseDto(savedOrder);
    }

    @Transactional
    @CacheEvict(value = {"plants", "plant"}, allEntries = true)
    public OrderResponseDto cancelOrder(Long id) {
        log.info("Request to cancel order with ID: {}", id);

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel an order with status: " + order.getStatus());
        }

        for (OrderItemEntity item : order.getItems()) {
            PlantEntity plant = item.getPlant();
            int restoredQuantity = plant.getQuantity() + item.getQuantity();
            plant.setQuantity(restoredQuantity);
            plantRepository.save(plant);
            log.info("Restored {} pcs of plant '{}' (ID: {}) back to stock", item.getQuantity(), plant.getName(), plant.getId());
        }

        order.setStatus(OrderStatus.CANCELLED);
        OrderEntity updatedOrder = orderRepository.save(order);

        log.info("Order with ID: {} has been successfully CANCELLED", id);
        return orderMapper.toResponseDto(updatedOrder);
    }

    @Transactional
    public void cancelExpiredOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);

        List<OrderEntity> expiredOrders = orderRepository.findAllByStatusAndCreatedAtBefore(
                OrderStatus.CREATED,
                threshold
        );

        if (expiredOrders.isEmpty()) {
            return;
        }

        log.info("Found {} expired orders in CREATED status to cancel", expiredOrders.size());

        for (OrderEntity order : expiredOrders) {
            log.info("Canceling expired Order ID: {}", order.getId());

            order.getItems().forEach(item -> {
                PlantEntity plant = item.getPlant();
                plant.setQuantity(plant.getQuantity() + item.getQuantity());
                plantRepository.save(plant);
            });

            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }
}