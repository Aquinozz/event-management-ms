package event_management.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:authdb")
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
