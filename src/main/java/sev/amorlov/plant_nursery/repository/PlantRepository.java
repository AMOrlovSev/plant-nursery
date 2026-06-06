package sev.amorlov.plant_nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sev.amorlov.plant_nursery.model.PlantEntity;

@Repository
public interface PlantRepository extends JpaRepository<PlantEntity, Long> {
}
