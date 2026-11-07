package event_management.event.repository;

import event_management.event.model.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {

    List<Attendee> findByEventoId(Long eventoId);

    long countByEventoId(Long eventoId);

    boolean existsByEmailAndEventoId(String email, Long eventoId);

    Optional<Attendee> findByEmailAndEventoId(String email, Long eventoId);
}
