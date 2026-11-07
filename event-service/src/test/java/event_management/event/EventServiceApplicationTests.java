package event_management.event;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:eventdb")
class EventServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
