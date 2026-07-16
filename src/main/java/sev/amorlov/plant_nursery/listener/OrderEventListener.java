package sev.amorlov.plant_nursery.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sev.amorlov.plant_nursery.event.OrderCreatedEvent;

@Slf4j
@Component
public class OrderEventListener {

    @Async("taskExecutor")
    @EventListener
    public void handleOrderCreatedSendEmail(OrderCreatedEvent event) {
        log.info("[THREAD: {}] Starting email dispatch for Order ID: {} to {}",
                Thread.currentThread().getName(), event.getOrderId(), event.getCustomerEmail());

        try {
            // Симулируем долгую отправку email (например, задержка сети)
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("[THREAD: {}] Email successfully sent for Order ID: {}",
                Thread.currentThread().getName(), event.getOrderId());
    }

    @Async("taskExecutor")
    @EventListener
    public void handleOrderCreatedTelegramNotification(OrderCreatedEvent event) {
        log.info("[THREAD: {}] Sending Telegram alert to admin for Order ID: {}",
                Thread.currentThread().getName(), event.getOrderId());
    }
}