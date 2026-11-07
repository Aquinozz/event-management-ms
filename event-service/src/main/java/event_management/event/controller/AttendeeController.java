package event_management.event.controller;

import event_management.event.dto.AttendeeRequestDto;
import event_management.event.dto.AttendeeResponseDto;
import event_management.event.dto.InscricaoRequestDto;
import event_management.event.service.AttendeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Participantes", description = "Operações relacionadas aos participantes dos eventos")
@RequiredArgsConstructor
public class AttendeeController {

    private final AttendeeService attendeeService;

    @Operation(summary = "Inscrever-se em um evento (público)")
    @PostMapping("/events/{eventoId}/inscrever")
    public ResponseEntity<AttendeeResponseDto> inscrever(@PathVariable Long eventoId,
                                                          @RequestBody @Valid InscricaoRequestDto dto) {
        AttendeeResponseDto participante = attendeeService.inscrever(eventoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(participante);
    }

    @Operation(summary = "Listar participantes de um evento")
    @GetMapping("/events/{eventoId}/participantes")
    public ResponseEntity<List<AttendeeResponseDto>> listarParticipantes(@PathVariable Long eventoId) {
        return ResponseEntity.ok(attendeeService.listarPorEvento(eventoId));
    }

    @Operation(summary = "Cadastrar participante manualmente (admin)")
    @PostMapping("/events/{eventoId}/participantes")
    public ResponseEntity<AttendeeResponseDto> cadastrar(@PathVariable Long eventoId,
                                                          @RequestBody @Valid AttendeeRequestDto dto) {
        AttendeeResponseDto participante = attendeeService.cadastrar(eventoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(participante);
    }

    @Operation(summary = "Remover participante de um evento")
    @DeleteMapping("/events/{eventoId}/participantes/{participanteId}")
    public ResponseEntity<Void> deletar(@PathVariable Long eventoId, @PathVariable Long participanteId) {
        attendeeService.deletar(eventoId, participanteId);
        return ResponseEntity.noContent().build();
    }
}
