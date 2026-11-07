package event_management.common.dto;

import java.time.LocalDateTime;

public record InscricaoCriadaMessage(Long eventoId, String eventoTitulo, LocalDateTime dataHora, String nome, String email) {
}
