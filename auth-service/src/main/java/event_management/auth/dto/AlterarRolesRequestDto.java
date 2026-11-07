package event_management.auth.dto;

import event_management.common.enums.RolesType;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record AlterarRolesRequestDto(@NotEmpty(message = "roles é obrigatório") Set<RolesType> roles) {
}
