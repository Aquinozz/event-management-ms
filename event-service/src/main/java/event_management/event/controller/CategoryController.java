package event_management.event.controller;

import event_management.event.dto.CategoryRequestDto;
import event_management.event.dto.CategoryResponseDto;
import event_management.event.service.CategoryService;
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
@Tag(name = "Categorias", description = "Operações relacionadas às categorias")
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Listar todas as categorias")
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> listar() {
        return ResponseEntity.ok(categoryService.listar());
    }

    @Operation(summary = "Buscar categoria por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.buscarPorId(id));
    }

    @Operation(summary = "Criar nova categoria")
    @PostMapping
    public ResponseEntity<CategoryResponseDto> criar(@RequestBody @Valid CategoryRequestDto dto) {
        CategoryResponseDto categoria = categoryService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @Operation(summary = "Atualizar categoria")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> atualizar(@PathVariable Long id,
                                                          @RequestBody @Valid CategoryRequestDto dto) {
        return ResponseEntity.ok(categoryService.atualizar(id, dto));
    }

    @Operation(summary = "Deletar categoria")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoryService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
