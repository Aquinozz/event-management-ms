package event_management.auth.repository;

import event_management.auth.model.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<RolesEntity, Long> {
    Optional<RolesEntity> findByNome(String nome);
    boolean existsByNome(String nome);
}
