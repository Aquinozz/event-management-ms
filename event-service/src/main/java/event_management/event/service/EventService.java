package event_management.event.service;

import event_management.event.dto.AlterarStatusRequestDto;
import event_management.event.dto.EventRequestDto;
import event_management.event.dto.EventResponseDto;
import event_management.event.enums.EventStatus;
import event_management.common.dto.UsuarioAutenticado;
import event_management.common.enums.RolesType;
import event_management.event.model.Attendee;
import event_management.event.model.Category;
import event_management.event.model.Event;
import event_management.event.repository.AttendeeRepository;
import event_management.event.repository.CategoryRepository;
import event_management.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final AttendeeRepository attendeeRepository;
    private final NotificationPublisher notificationPublisher;

    public Page<EventResponseDto> listar(Long categoriaId, EventStatus status,
                                          LocalDateTime dataInicio, LocalDateTime dataFim,
                                          String busca, Pageable pageable) {
        log.info("Listando eventos com filtros");

        Page<Event> eventos = eventRepository.filtrar(categoriaId, status, dataInicio, dataFim, busca, pageable);
        return eventos.map(this::toResponseDto);
    }

    public Page<EventResponseDto> listarPorCriador(String email, Pageable pageable) {
        return eventRepository.findByCriadorEmail(email, pageable).map(this::toResponseDto);
    }

    public Page<EventResponseDto> listarPorCategoria(Long categoriaId, Pageable pageable) {
        if (!categoryRepository.existsById(categoriaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada");
        }
        return eventRepository.filtrar(categoriaId, null, null, null, null, pageable).map(this::toResponseDto);
    }

    public EventResponseDto buscarPorId(Long id) {
        log.info("Buscando evento por ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
        return toResponseDto(event);
    }

    @Transactional
    public EventResponseDto criar(EventRequestDto dto, Authentication authentication) {
        log.info("Criando evento para usuário: {}", authentication.getName());

        Category categoria = categoryRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));

        if (!(authentication.getDetails() instanceof UsuarioAutenticado criador) || criador.id() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sem dados do usuário, faça login novamente");
        }

        EventStatus status = calcularStatus(dto.getDataHora());

        Event event = Event.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .dataHora(dto.getDataHora())
                .local(dto.getLocal())
                .status(status)
                .qtdVagas(dto.getQtdVagas())
                .categoria(categoria)
                .criadorId(criador.id())
                .criadorEmail(criador.email())
                .criadorNome(criador.nome())
                .build();

        event = eventRepository.save(event);
        log.info("Evento criado com ID: {}", event.getId());
        return toResponseDto(event);
    }

    @Transactional
    public EventResponseDto atualizar(Long id, EventRequestDto dto, Authentication authentication) {
        log.info("Atualizando evento ID: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
        verificarPermissao(event, authentication);

        Category categoria = categoryRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));

        event.setTitulo(dto.getTitulo());
        event.setDescricao(dto.getDescricao());
        event.setDataHora(dto.getDataHora());
        event.setLocal(dto.getLocal());
        event.setStatus(calcularStatus(dto.getDataHora()));
        event.setQtdVagas(dto.getQtdVagas());
        event.setCategoria(categoria);

        event = eventRepository.save(event);
        return toResponseDto(event);
    }

    @Transactional
    public void deletar(Long id, Authentication authentication) {
        log.info("Deletando evento ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
        verificarPermissao(event, authentication);
        eventRepository.delete(event);
    }

    @Transactional
    public EventResponseDto alterarStatus(Long id, AlterarStatusRequestDto dto, Authentication authentication) {
        log.info("Alterando status do evento ID: {} para {}", id, dto.getStatus());

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
        verificarPermissao(event, authentication);

        event.setStatus(dto.getStatus());
        event = eventRepository.save(event);

        if (dto.getStatus() == EventStatus.CANCELLED) {
            List<String> emails = attendeeRepository.findByEventoId(id).stream()
                    .map(Attendee::getEmail)
                    .toList();
            notificationPublisher.eventoCancelado(event, emails);
        }
        return toResponseDto(event);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void atualizarStatusAutomaticamente() {
        log.info("Atualizando status dos eventos automaticamente");
        LocalDateTime now = LocalDateTime.now();

        eventRepository.findByDataHoraBeforeAndStatusNot(now.minusHours(2), EventStatus.FINISHED)
                .forEach(event -> {
                    event.setStatus(EventStatus.FINISHED);
                    eventRepository.save(event);
                });
    }

    private void verificarPermissao(Event event, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RolesType.ROLE_ADMIN.name()));
        if (!admin && !event.getCriadorEmail().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissão para alterar este evento");
        }
    }

    private EventStatus calcularStatus(LocalDateTime dataHora) {
        LocalDateTime now = LocalDateTime.now();

        if (dataHora.isAfter(now)) {
            return EventStatus.UPCOMING;
        } else if (dataHora.isBefore(now) && dataHora.plusHours(2).isAfter(now)) {
            return EventStatus.ONGOING;
        } else {
            return EventStatus.FINISHED;
        }
    }

    private EventResponseDto toResponseDto(Event event) {
        long totalInscritos = attendeeRepository.countByEventoId(event.getId());
        int vagasRestantes = event.getQtdVagas() - (int) totalInscritos;

        return EventResponseDto.builder()
                .id(event.getId())
                .titulo(event.getTitulo())
                .descricao(event.getDescricao())
                .dataHora(event.getDataHora())
                .local(event.getLocal())
                .status(event.getStatus())
                .qtdVagas(event.getQtdVagas())
                .vagasRestantes(Math.max(vagasRestantes, 0))
                .categoria(event.getCategoria().getNome())
                .categoriaId(event.getCategoria().getId())
                .criador(event.getCriadorNome())
                .criadorId(event.getCriadorId())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
