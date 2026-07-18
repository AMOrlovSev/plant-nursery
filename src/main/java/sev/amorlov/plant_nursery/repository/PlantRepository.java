package sev.amorlov.plant_nursery.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sev.amorlov.plant_nursery.model.PlantEntity;

import java.util.Optional;

@Repository
public interface PlantRepository extends JpaRepository<PlantEntity, Long>, JpaSpecificationExecutor<PlantEntity> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PlantEntity p where p.id = :id")
    Optional<PlantEntity> findByIdForUpdate(Long id);
}
