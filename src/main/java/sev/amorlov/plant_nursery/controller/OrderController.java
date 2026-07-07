package sev.amorlov.plant_nursery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sev.amorlov.plant_nursery.dto.OrderRequestDto;
import sev.amorlov.plant_nursery.dto.OrderResponseDto;
import sev.amorlov.plant_nursery.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto dto) {
        OrderResponseDto response = orderService.createOrder(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}