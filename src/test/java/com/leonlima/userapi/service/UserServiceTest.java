package com.leonlima.userapi.service;

import com.leonlima.userapi.dto.UserDTO;
import com.leonlima.userapi.model.User;
import com.leonlima.userapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — testes unitários")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User regularUser;
    private User adminUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        regularUser = User.builder()
                .id(1L).name("Leon Lima").email("leon@email.com")
                .password("hash").role(User.Role.USER)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        adminUser = User.builder()
                .id(2L).name("Admin").email("admin@email.com")
                .password("hash").role(User.Role.ADMIN)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        otherUser = User.builder()
                .id(3L).name("Outro").email("outro@email.com")
                .password("hash").role(User.Role.USER)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("Deve retornar perfil quando o próprio usuário solicita")
    void getProfile_ownProfile_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(regularUser));
        when(userRepository.findByEmail("leon@email.com")).thenReturn(Optional.of(regularUser));

        UserDTO.ProfileResponse response = userService.getProfile(1L, "leon@email.com");

        assertThat(response.getEmail()).isEqualTo("leon@email.com");
        assertThat(response.getName()).isEqualTo("Leon Lima");
    }

    @Test
    @DisplayName("ADMIN deve acessar qualquer perfil")
    void getProfile_adminAccess_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(regularUser));
        when(userRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adminUser));

        UserDTO.ProfileResponse response = userService.getProfile(1L, "admin@email.com");

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao acessar perfil de outro usuário")
    void getProfile_unauthorizedAccess_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(regularUser));
        when(userRepository.findByEmail("outro@email.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> userService.getProfile(1L, "outro@email.com"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException para usuário inexistente")
    void getProfile_userNotFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile(99L, "leon@email.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar apenas os campos enviados na requisição")
    void updateProfile_partialUpdate_success() {
        UserDTO.UpdateRequest request = new UserDTO.UpdateRequest();
        request.setName("Leon Atualizado");

        when(userRepository.findById(1L)).thenReturn(Optional.of(regularUser));
        when(userRepository.findByEmail("leon@email.com")).thenReturn(Optional.of(regularUser));
        when(userRepository.save(any(User.class))).thenReturn(regularUser);

        userService.updateProfile(1L, request, "leon@email.com");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar o usuário corretamente")
    void deleteUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(regularUser));
        when(userRepository.findByEmail("leon@email.com")).thenReturn(Optional.of(regularUser));

        userService.deleteUser(1L, "leon@email.com");

        verify(userRepository).delete(regularUser);
    }

    @Test
    @DisplayName("Deve retornar todos os usuários cadastrados")
    void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(regularUser, adminUser, otherUser));

        assertThat(userService.getAllUsers()).hasSize(3);
    }
}
