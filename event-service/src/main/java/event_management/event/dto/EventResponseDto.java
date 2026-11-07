package event_management.event.dto;

import event_management.event.enums.EventStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResponseDto {

    private Long id;
    private String titulo;
    private String descricao;
    private LocalDateTime dataHora;
    private String local;
    private EventStatus status;
    private Integer qtdVagas;
    private Integer vagasRestantes;
    private String categoria;
    private Long categoriaId;
    private String criador;
    private Long criadorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
