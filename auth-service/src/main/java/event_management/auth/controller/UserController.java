package event_management.auth.controller;

import event_management.auth.dto.AlterarRolesRequestDto;
import event_management.auth.dto.UserResponseDto;
import event_management.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Administração de usuários e roles")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Listar usuários", description = "Lista paginada de usuários (admin)")
    @GetMapping
    public Page<UserResponseDto> listar(@PageableDefault(size = 10) Pageable pageable) {
        return userService.listar(pageable);
    }

    @Operation(summary = "Alterar roles", description = "Define as roles do usuário; vale a partir do próximo login (admin)")
    @PatchMapping("/{id}/roles")
    public UserResponseDto alterarRoles(@PathVariable Long id,
                                        @Valid @RequestBody AlterarRolesRequestDto request,
                                        Authentication authentication) {
        return userService.alterarRoles(id, request, authentication.getName());
    }
}
