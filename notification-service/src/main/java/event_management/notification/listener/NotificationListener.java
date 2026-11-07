package event_management.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import event_management.common.constants.KafkaTopics;
import event_management.common.dto.EventoCanceladoMessage;
import event_management.common.dto.InscricaoCriadaMessage;
import event_management.notification.model.Notification;
import event_management.notification.model.NotificationType;
import event_management.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.INSCRICAO_CRIADA, groupId = "notification-service")
    public void onInscricaoCriada(String messageJson) {
        InscricaoCriadaMessage msg;
        try {
            msg = objectMapper.readValue(messageJson, InscricaoCriadaMessage.class);
        } catch (Exception e) {
            log.error("Mensagem de inscrição inválida: {}", messageJson);
            return;
        }

        salvar(NotificationType.INSCRICAO_CONFIRMADA, msg.eventoId(), msg.email(),
                "Olá " + msg.nome() + ", sua inscrição em \"" + msg.eventoTitulo() + "\" (" + msg.dataHora() + ") está confirmada.");
    }

    @KafkaListener(topics = KafkaTopics.EVENTO_CANCELADO, groupId = "notification-service")
    public void onEventoCancelado(String messageJson) {
        EventoCanceladoMessage msg;
        try {
            msg = objectMapper.readValue(messageJson, EventoCanceladoMessage.class);
        } catch (Exception e) {
            log.error("Mensagem de cancelamento inválida: {}", messageJson);
            return;
        }

        msg.emails().forEach(email -> salvar(NotificationType.EVENTO_CANCELADO, msg.eventoId(), email,
                "O evento \"" + msg.eventoTitulo() + "\" foi cancelado."));
    }

    private void salvar(NotificationType tipo, Long eventoId, String destinatario, String mensagem) {
        Notification notification = new Notification();
        notification.setTipo(tipo);
        notification.setEventoId(eventoId);
        notification.setDestinatario(destinatario);
        notification.setMensagem(mensagem);
        notificationRepository.save(notification);
        log.info("E-mail enviado para {}: {}", destinatario, tipo);
    }
}
