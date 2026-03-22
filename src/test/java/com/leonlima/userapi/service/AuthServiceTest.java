package com.leonlima.userapi.service;

import com.leonlima.userapi.dto.UserDTO;
import com.leonlima.userapi.exception.EmailAlreadyExistsException;
import com.leonlima.userapi.model.User;
import com.leonlima.userapi.repository.UserRepository;
import com.leonlima.userapi.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — testes unitários")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User savedUser;
    private UserDTO.RegisterRequest registerRequest;
    private UserDTO.LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        savedUser = User.builder()
                .id(1L).name("Leon Lima").email("leon@email.com")
                .password("hash").role(User.Role.USER)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        registerRequest = new UserDTO.RegisterRequest();
        registerRequest.setName("Leon Lima");
        registerRequest.setEmail("leon@email.com");
        registerRequest.setPassword("senha123");

        loginRequest = new UserDTO.LoginRequest();
        loginRequest.setEmail("leon@email.com");
        loginRequest.setPassword("senha123");
    }

    @Test
    @DisplayName("Deve registrar usuário e retornar token JWT")
    void register_success_returnsToken() {
        when(userRepository.existsByEmail("leon@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDetails mockDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("leon@email.com")).thenReturn(mockDetails);
        when(jwtService.generateToken(mockDetails)).thenReturn("token.mock");

        UserDTO.AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("token.mock");
        assertThat(response.getType()).isEqualTo("Bearer");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar EmailAlreadyExistsException para email duplicado")
    void register_duplicateEmail_throwsException() {
        when(userRepository.existsByEmail("leon@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve autenticar e retornar token JWT")
    void login_validCredentials_returnsToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("leon@email.com")).thenReturn(Optional.of(savedUser));

        UserDetails mockDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("leon@email.com")).thenReturn(mockDetails);
        when(jwtService.generateToken(mockDetails)).thenReturn("token.mock");

        UserDTO.AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("token.mock");
        assertThat(response.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException para credenciais inválidas")
    void login_invalidCredentials_throwsException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).generateToken(any());
    }
}
