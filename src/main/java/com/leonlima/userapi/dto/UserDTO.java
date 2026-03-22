package com.leonlima.userapi.dto;

import com.leonlima.userapi.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class UserDTO {

    @Data
    public static class RegisterRequest {

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        private String name;

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato de email inválido")
        private String email;

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        private String password;

        private String phone;
        private String bio;
    }

    @Data
    public static class LoginRequest {

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato de email inválido")
        private String email;

        @NotBlank(message = "Senha é obrigatória")
        private String password;
    }

    @Data
    public static class UpdateRequest {
        @Size(min = 2, max = 100)
        private String name;
        private String phone;
        private String bio;
    }

    @Data
    @Builder
    public static class AuthResponse {
        private String token;
        private String type;
        private Long userId;
        private String name;
        private String email;
        private String role;
    }

    @Data
    @Builder
    public static class ProfileResponse {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String bio;
        private String role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Centraliza o mapeamento entidade → DTO em um único lugar
        public static ProfileResponse fromEntity(User user) {
            return ProfileResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .bio(user.getBio())
                    .role(user.getRole().name())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }
}
