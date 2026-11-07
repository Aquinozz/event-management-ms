package event_management.notification.controller;

import event_management.notification.dto.NotificationResponse;
import event_management.notification.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Notificações geradas a partir dos eventos")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @Operation(summary = "Listar notificações", description = "Filtra por destinatário quando o email é informado (admin)")
    @GetMapping
    public Page<NotificationResponse> listar(@RequestParam(required = false) String email,
                                             @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (email == null) {
            return notificationRepository.findAll(pageable).map(NotificationResponse::from);
        }
        return notificationRepository.findByDestinatario(email, pageable).map(NotificationResponse::from);
    }
}
