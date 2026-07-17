package sev.amorlov.plant_nursery.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sev.amorlov.plant_nursery.service.OrderService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCleanupScheduler {

    private final OrderService orderService;

    // Запускать задачу каждые 15 минут (900 000 миллисекунд)
    // initialDelay = 5000 означает, что первый запуск произойдет через 5 секунд после старта приложения
    @Scheduled(fixedRate = 900000, initialDelay = 5000)
    public void cleanupExpiredOrders() {
        log.info("Starting scheduled task: Cleanup expired pending orders...");
        try {
            orderService.cancelExpiredOrders();
            log.info("Scheduled task 'Cleanup expired orders' finished successfully.");
        } catch (Exception e) {
            log.error("Error occurred during expired orders cleanup", e);
        }
    }
}