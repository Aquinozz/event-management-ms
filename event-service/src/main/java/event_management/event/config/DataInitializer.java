package event_management.event.config;

import event_management.event.model.Category;
import event_management.event.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryRepository categoryRepository;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            if (!categoryRepository.existsByNome("Palestra")) {
                categoryRepository.save(Category.builder().nome("Palestra").descricao("Palestras e talks").build());
            }
            if (!categoryRepository.existsByNome("Workshop")) {
                categoryRepository.save(Category.builder().nome("Workshop").descricao("Workshops práticos").build());
            }
            if (!categoryRepository.existsByNome("Meetup")) {
                categoryRepository.save(Category.builder().nome("Meetup").descricao("Encontros e networking").build());
            }
            if (!categoryRepository.existsByNome("Conferência")) {
                categoryRepository.save(Category.builder().nome("Conferência").descricao("Conferências e congressos").build());
            }
        };
    }
}
