package event_management.event.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendeeResponseDto {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDateTime createdAt;
}
