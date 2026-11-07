package event_management.event.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InscricaoRequestDto {

    @NotBlank
    private String nome;

    @NotBlank
    @Email
    private String email;

    private String telefone;
}
