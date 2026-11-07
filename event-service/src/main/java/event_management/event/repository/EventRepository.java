package event_management.event.repository;

import event_management.event.enums.EventStatus;
import event_management.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByCategoriaId(Long categoriaId, Pageable pageable);

    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
           "(:categoriaId IS NULL OR e.categoria.id = :categoriaId) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:dataInicio IS NULL OR e.dataHora >= :dataInicio) AND " +
           "(:dataFim IS NULL OR e.dataHora <= :dataFim) AND " +
           "(:busca IS NULL OR LOWER(e.titulo) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(e.descricao) LIKE LOWER(CONCAT('%', :busca, '%')))")
    Page<Event> filtrar(
            @Param("categoriaId") Long categoriaId,
            @Param("status") EventStatus status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("busca") String busca,
            Pageable pageable
    );

    List<Event> findByDataHoraBeforeAndStatusNot(LocalDateTime data, EventStatus status);
}
