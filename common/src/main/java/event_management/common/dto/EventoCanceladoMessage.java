package event_management.common.dto;

import java.util.List;

public record EventoCanceladoMessage(Long eventoId, String eventoTitulo, List<String> emails) {
}
