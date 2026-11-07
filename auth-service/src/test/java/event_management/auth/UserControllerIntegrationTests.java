package event_management.auth;

import event_management.auth.config.TokenProvider;
import event_management.auth.model.RolesEntity;
import event_management.auth.model.Users;
import event_management.auth.repository.RolesRepository;
import event_management.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:authdb-users")
@AutoConfigureMockMvc
class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    void adminPromoveUsuario() throws Exception {
        Users admin = userRepository.findByEmail("admin@email.com").orElseThrow();
        Users user = criarUsuario();

        mockMvc.perform(patch("/users/{id}/roles", user.getId())
                        .header("Authorization", "Bearer " + tokenProvider.gerarToken(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roles\":[\"ROLE_USER\",\"ROLE_ADMIN\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.roles[1]").value("ROLE_USER"));
    }

    @Test
    void usuarioComumRecebe403() throws Exception {
        Users user = criarUsuario();

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + tokenProvider.gerarToken(user)))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/users/{id}/roles", user.getId())
                        .header("Authorization", "Bearer " + tokenProvider.gerarToken(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roles\":[\"ROLE_ADMIN\"]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminNaoRemoveProprioAcesso() throws Exception {
        Users admin = userRepository.findByEmail("admin@email.com").orElseThrow();

        mockMvc.perform(patch("/users/{id}/roles", admin.getId())
                        .header("Authorization", "Bearer " + tokenProvider.gerarToken(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roles\":[\"ROLE_USER\"]}"))
                .andExpect(status().isConflict());
    }

    @Test
    void semTokenRecebe401() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/auth/validate")).andExpect(status().isUnauthorized());
    }

    private Users criarUsuario() {
        RolesEntity roleUser = rolesRepository.findByNome("ROLE_USER").orElseThrow();
        return userRepository.save(Users.builder()
                .nome("Fulano")
                .email(UUID.randomUUID() + "@email.com")
                .senha("x")
                .roles(Set.of(roleUser))
                .build());
    }
}
