package event_management.auth.service;

import event_management.auth.config.TokenBlacklist;
import event_management.auth.config.TokenProvider;
import event_management.auth.dto.LoginRequestDto;
import event_management.auth.dto.RegisterRequestDto;
import event_management.auth.dto.TokenResponseDto;
import event_management.auth.dto.UserResponseDto;
import event_management.common.enums.RolesType;
import event_management.auth.model.RolesEntity;
import event_management.auth.model.Users;
import event_management.auth.repository.RolesRepository;
import event_management.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final TokenBlacklist tokenBlacklist;

    public void register(RegisterRequestDto dto) {
        log.info("Tentando registrar usuário com email: {}", dto.getEmail());

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email já cadastrado: {}", dto.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já cadastrado");
        }

        RolesEntity role = rolesRepository.findByNome(RolesType.ROLE_USER.name())
                .orElseGet(() -> rolesRepository.save(
                        RolesEntity.builder()
                                .nome(RolesType.ROLE_USER.name())
                                .build()
                ));

        userRepository.save(
                Users.builder()
                        .nome(dto.getNome())
                        .email(dto.getEmail())
                        .senha(passwordEncoder.encode(dto.getSenha()))
                        .roles(Set.of(role))
                        .build()
        );

        log.info("Usuário registrado com sucesso: {}", dto.getEmail());
    }

    public TokenResponseDto login(LoginRequestDto dto) {
        log.info("Tentativa de login para email: {}", dto.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );
            Users user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));
            String token = tokenProvider.gerarToken(user);
            log.info("Login realizado para: {}", dto.getEmail());
            return new TokenResponseDto(token, tokenProvider.getExpirationTime());
        } catch (BadCredentialsException e) {
            log.warn("Credenciais inválidas para: {}", dto.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }
    }

    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            tokenBlacklist.invalidate(token.substring(7));
        }
        log.info("Logout realizado com sucesso");
    }

    public UserResponseDto me(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponseDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }
}
