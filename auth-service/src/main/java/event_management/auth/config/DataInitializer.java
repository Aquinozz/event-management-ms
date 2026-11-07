package event_management.auth.config;

import event_management.auth.model.RolesEntity;
import event_management.auth.model.Users;
import event_management.auth.repository.RolesRepository;
import event_management.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RolesRepository rolesRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            if (!rolesRepository.existsByNome("ROLE_ADMIN")) {
                rolesRepository.save(new RolesEntity("ROLE_ADMIN"));
            }
            if (!rolesRepository.existsByNome("ROLE_USER")) {
                rolesRepository.save(new RolesEntity("ROLE_USER"));
            }

            if (!userRepository.existsByEmail("admin@email.com")) {
                RolesEntity adminRole = rolesRepository.findByNome("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("ROLE_ADMIN não encontrada"));
                Users admin = Users.builder()
                        .nome("Admin")
                        .email("admin@email.com")
                        .senha(passwordEncoder.encode("123456"))
                        .roles(Set.of(adminRole))
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
