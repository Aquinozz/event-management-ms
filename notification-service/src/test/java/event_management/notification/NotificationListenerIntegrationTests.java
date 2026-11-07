package event_management.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import event_management.common.constants.KafkaTopics;
import event_management.common.dto.EventoCanceladoMessage;
import event_management.common.dto.InscricaoCriadaMessage;
import event_management.notification.model.Notification;
import event_management.notification.model.NotificationType;
import event_management.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:notificationdb",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
})
@Testcontainers(disabledWithoutDocker = true)
class NotificationListenerIntegrationTests {

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void inscricaoGeraNotificacaoDeConfirmacao() throws Exception {
        InscricaoCriadaMessage msg = new InscricaoCriadaMessage(1L, "Meetup Java",
                LocalDateTime.of(2026, 12, 1, 19, 0), "Maria", "maria@email.com");

        kafkaTemplate.send(KafkaTopics.INSCRICAO_CRIADA, "1", objectMapper.writeValueAsString(msg)).get();

        List<Notification> salvas = aguardar("maria@email.com", 1);
        assertThat(salvas).hasSize(1);
        assertThat(salvas.get(0).getTipo()).isEqualTo(NotificationType.INSCRICAO_CONFIRMADA);
        assertThat(salvas.get(0).getMensagem()).contains("Meetup Java");
    }

    @Test
    void cancelamentoGeraUmaNotificacaoPorInscrito() throws Exception {
        EventoCanceladoMessage msg = new EventoCanceladoMessage(2L, "Workshop Spring",
                List.of("ana@email.com", "joao@email.com"));

        kafkaTemplate.send(KafkaTopics.EVENTO_CANCELADO, "2", objectMapper.writeValueAsString(msg)).get();

        assertThat(aguardar("ana@email.com", 1)).extracting(Notification::getTipo)
                .containsExactly(NotificationType.EVENTO_CANCELADO);
        assertThat(aguardar("joao@email.com", 1)).hasSize(1);
    }

    private List<Notification> aguardar(String email, int quantidade) throws InterruptedException {
        List<Notification> salvas = List.of();
        long deadline = System.currentTimeMillis() + 20_000;
        while (System.currentTimeMillis() < deadline && salvas.size() < quantidade) {
            salvas = notificationRepository.findByDestinatario(email, Pageable.unpaged()).getContent();
            if (salvas.size() < quantidade) {
                Thread.sleep(300);
            }
        }
        return salvas;
    }
}
