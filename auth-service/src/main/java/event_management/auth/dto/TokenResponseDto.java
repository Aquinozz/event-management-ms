package event_management.auth.dto;

public record TokenResponseDto(String token, long expiresIn) {
}
