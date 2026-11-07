package event_management.event.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestDto {

    @NotBlank
    private String titulo;

    private String descricao;

    @NotNull
    private LocalDateTime dataHora;

    @NotBlank
    private String local;

    @NotNull
    @Min(value = 1)
    private Integer qtdVagas;

    @NotNull
    private Long categoriaId;
}
