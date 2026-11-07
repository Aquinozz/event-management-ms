package event_management.notification.dto;

import event_management.notification.model.Notification;
import event_management.notification.model.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(Long id, NotificationType tipo, Long eventoId, String destinatario,
                                   String mensagem, LocalDateTime createdAt) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTipo(),
                notification.getEventoId(),
                notification.getDestinatario(),
                notification.getMensagem(),
                notification.getCreatedAt());
    }
}
