package event_management.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

@Slf4j
public class AuthServiceClient {

    private final RestClient restClient;

    public AuthServiceClient(String authUrl) {
        this.restClient = RestClient.create(authUrl);
    }

    public boolean isRevogado(String token) {
        try {
            restClient.get()
                    .uri("/auth/validate")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
            return false;
        } catch (Exception e) {
            log.warn("Token recusado pelo auth-service: {}", e.getMessage());
            return true;
        }
    }
}
