package event_management.auth.dto;

import event_management.auth.model.RolesEntity;
import event_management.auth.model.Users;

import java.util.List;

public record UserResponseDto(Long id, String nome, String email, List<String> roles) {

    public static UserResponseDto from(Users user) {
        return new UserResponseDto(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getRoles().stream().map(RolesEntity::getNome).sorted().toList());
    }
}
