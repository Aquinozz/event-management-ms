package event_management.event.dto;

import event_management.event.enums.EventStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlterarStatusRequestDto {

    @NotNull
    private EventStatus status;
}
