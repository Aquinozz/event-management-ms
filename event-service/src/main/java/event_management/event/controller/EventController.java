package event_management.event.controller;

import event_management.event.dto.AlterarStatusRequestDto;
import event_management.event.dto.EventRequestDto;
import event_management.event.dto.EventResponseDto;
import event_management.event.enums.EventStatus;
import event_management.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@Tag(name = "Eventos", description = "Operações relacionadas aos eventos")
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Listar eventos com filtros")
    @GetMapping
    public ResponseEntity<Page<EventResponseDto>> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<EventResponseDto> eventos = eventService.listar(
                categoriaId, status, dataInicio, dataFim, busca, pageable
        );
        return ResponseEntity.ok(eventos);
    }

    @Operation(summary = "Listar eventos criados pelo usuário logado")
    @GetMapping("/me")
    public ResponseEntity<Page<EventResponseDto>> meusEventos(@PageableDefault(size = 10) Pageable pageable,
                                                             Authentication authentication) {
        return ResponseEntity.ok(eventService.listarPorCriador(authentication.getName(), pageable));
    }

    @Operation(summary = "Buscar evento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> buscarPorId(@PathVariable Long id) {
        EventResponseDto evento = eventService.buscarPorId(id);
        return ResponseEntity.ok(evento);
    }

    @Operation(summary = "Criar novo evento")
    @PostMapping
    public ResponseEntity<EventResponseDto> criar(@RequestBody @Valid EventRequestDto dto,
                                                   Authentication authentication) {
        EventResponseDto evento = eventService.criar(dto, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(evento);
    }

    @Operation(summary = "Atualizar evento")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> atualizar(@PathVariable Long id,
                                                       @RequestBody @Valid EventRequestDto dto,
                                                       Authentication authentication) {
        EventResponseDto evento = eventService.atualizar(id, dto, authentication);
        return ResponseEntity.ok(evento);
    }

    @Operation(summary = "Deletar evento")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, Authentication authentication) {
        eventService.deletar(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Alterar status do evento")
    @PatchMapping("/{id}/status")
    public ResponseEntity<EventResponseDto> alterarStatus(@PathVariable Long id,
                                                           @RequestBody @Valid AlterarStatusRequestDto dto,
                                                           Authentication authentication) {
        EventResponseDto evento = eventService.alterarStatus(id, dto, authentication);
        return ResponseEntity.ok(evento);
    }
}
