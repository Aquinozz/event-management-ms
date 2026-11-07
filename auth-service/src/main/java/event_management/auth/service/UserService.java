package event_management.auth.service;

import event_management.auth.dto.AlterarRolesRequestDto;
import event_management.auth.dto.UserResponseDto;
import event_management.auth.model.RolesEntity;
import event_management.auth.model.Users;
import event_management.auth.repository.RolesRepository;
import event_management.auth.repository.UserRepository;
import event_management.common.enums.RolesType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;

    public Page<UserResponseDto> listar(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponseDto::from);
    }

    @Transactional
    public UserResponseDto alterarRoles(Long id, AlterarRolesRequestDto request, String emailLogado) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (user.getEmail().equals(emailLogado) && !request.roles().contains(RolesType.ROLE_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível remover o próprio acesso de admin");
        }

        Set<RolesEntity> roles = request.roles().stream()
                .map(role -> rolesRepository.findByNome(role.name())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role inválida: " + role)))
                .collect(Collectors.toCollection(HashSet::new));

        user.setRoles(roles);
        return UserResponseDto.from(userRepository.save(user));
    }
}
