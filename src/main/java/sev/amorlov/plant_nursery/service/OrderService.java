package sev.amorlov.plant_nursery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sev.amorlov.plant_nursery.dto.*;
import sev.amorlov.plant_nursery.exception.InsufficientStockException;
import sev.amorlov.plant_nursery.model.*;
import sev.amorlov.plant_nursery.repository.OrderRepository;
import sev.amorlov.plant_nursery.repository.PlantRepository;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final PlantRepository plantRepository;
    private final OrderMapper orderMapper;

    @Transactional
    @CacheEvict(value = {"plants", "plant"}, allEntries = true) // Сбрасываем кэш, так как остатки изменятся
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        log.info("Starting order creation for customer: {}", dto.customerEmail());

        OrderEntity order = new OrderEntity();
        order.setCustomerEmail(dto.customerEmail());
        order.setStatus(OrderStatus.CREATED);

        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : dto.items()) {
            PlantEntity plant = plantRepository.findById(itemDto.plantId())
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

        log.info("Order successfully created with ID: {}, Total Price: {}", savedOrder.getId(), savedOrder.getTotalPrice());
        return orderMapper.toResponseDto(savedOrder);
    }
}