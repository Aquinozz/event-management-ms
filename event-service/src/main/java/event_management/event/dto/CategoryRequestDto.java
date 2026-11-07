package event_management.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequestDto {

    @NotBlank
    private String nome;

    private String descricao;
}
