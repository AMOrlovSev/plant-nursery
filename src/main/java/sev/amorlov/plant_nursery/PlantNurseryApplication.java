package sev.amorlov.plant_nursery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class PlantNurseryApplication {

    public static void main(String[] args) {
		SpringApplication.run(PlantNurseryApplication.class, args);
    }

}
