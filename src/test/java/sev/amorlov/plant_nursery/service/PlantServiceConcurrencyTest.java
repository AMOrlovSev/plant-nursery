package sev.amorlov.plant_nursery.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import sev.amorlov.plant_nursery.dto.PlantRequestDto;
import sev.amorlov.plant_nursery.repository.PlantRepository;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PlantServiceConcurrencyTest {

    @Autowired
    private PlantService plantService;

    @Autowired
    private PlantRepository plantRepository;

    private Long testPlantId;

    @BeforeEach
    void setUp() {
        plantRepository.deleteAll();
        var dto = new PlantRequestDto("Фикус", "Комнатные", new BigDecimal("1000"), 1, null);
        testPlantId = plantService.savePlant(dto).id();
    }

    @Test
    void shouldThrowExceptionWhenTwoThreadsSellLastPlantSimultaneously() throws InterruptedException {
        int numberOfThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1); // Ждем команды "Старт"
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads); // Ждем завершения обоих

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); // Ждем старта
                    plantService.sellPlant(testPlantId, 1);
                    successCount.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException e) {
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    // Обработка других ошибок
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        latch.countDown(); // Даем команду "Старт" всем потокам одновременно!
        doneLatch.await(); // Ждем пока оба завершат работу

        // Проверяем: ровно 1 успешная продажа, ровно 1 ошибка оптимистичной блокировки
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        // Проверяем, что остаток в базе равен 0
        var plant = plantRepository.findById(testPlantId).orElseThrow();
        assertThat(plant.getQuantity()).isEqualTo(0);
    }
}