package event_management.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import event_management.common.constants.KafkaTopics;
import event_management.common.dto.EventoCanceladoMessage;
import event_management.common.dto.InscricaoCriadaMessage;
import event_management.event.model.Attendee;
import event_management.event.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void inscricaoCriada(Event event, Attendee attendee) {
        enviar(KafkaTopics.INSCRICAO_CRIADA, event.getId(), new InscricaoCriadaMessage(
                event.getId(), event.getTitulo(), event.getDataHora(), attendee.getNome(), attendee.getEmail()));
    }

    public void eventoCancelado(Event event, List<String> emails) {
        enviar(KafkaTopics.EVENTO_CANCELADO, event.getId(),
                new EventoCanceladoMessage(event.getId(), event.getTitulo(), emails));
    }

    private void enviar(String topic, Long eventoId, Object payload) {
        try {
            kafkaTemplate.send(topic, String.valueOf(eventoId), objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao serializar notificação", e);
        }
    }
}
