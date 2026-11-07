package event_management.auth.controller;

import event_management.auth.dto.LoginRequestDto;
import event_management.auth.dto.RegisterRequestDto;
import event_management.auth.dto.TokenResponseDto;
import event_management.auth.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Autenticação", description = "Operações de login, registro e logout")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Registrar novo usuário")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDto dto) {
        authenticationService.register(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Realizar login")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        TokenResponseDto response = authenticationService.login(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Realizar logout")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        authenticationService.logout(header);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Validar token", description = "Retorna 200 se o token é válido e não foi revogado (uso interno dos serviços)")
    @GetMapping("/validate")
    public ResponseEntity<Void> validate() {
        return ResponseEntity.ok().build();
    }
}
