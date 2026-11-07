package event_management.event.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponseDto {

    private Long id;
    private String nome;
    private String descricao;
}
