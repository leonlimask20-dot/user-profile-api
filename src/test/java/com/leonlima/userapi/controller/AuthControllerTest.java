package com.leonlima.userapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonlima.userapi.dto.UserDTO;
import com.leonlima.userapi.exception.EmailAlreadyExistsException;
import com.leonlima.userapi.security.JwtAuthFilter;
import com.leonlima.userapi.security.JwtService;
import com.leonlima.userapi.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthFilter.class
    )
)
@DisplayName("AuthController - testes MockMvc")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private UserDTO.AuthResponse respostaAuth() {
        return UserDTO.AuthResponse.builder()
            .token("token.mock")
            .type("Bearer")
            .userId(1L)
            .name("Leon Lima")
            .email("leon@email.com")
            .role("USER")
            .build();
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register - deve retornar 201 com token")
    void register_dadosValidos_retorna201() throws Exception {
        UserDTO.RegisterRequest req = new UserDTO.RegisterRequest();
        req.setName("Leon Lima");
        req.setEmail("leon@email.com");
        req.setPassword("senha123");

        when(authService.register(any())).thenReturn(respostaAuth());

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").value("token.mock"))
            .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register - deve retornar 400 quando dados invalidos")
    void register_dadosInvalidos_retorna400() throws Exception {
        UserDTO.RegisterRequest req = new UserDTO.RegisterRequest();
        req.setName("");
        req.setEmail("email-invalido");
        req.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register - deve retornar 409 quando email duplicado")
    void register_emailDuplicado_retorna409() throws Exception {
        UserDTO.RegisterRequest req = new UserDTO.RegisterRequest();
        req.setName("Leon Lima");
        req.setEmail("leon@email.com");
        req.setPassword("senha123");

        when(authService.register(any()))
            .thenThrow(new EmailAlreadyExistsException("Email ja cadastrado"));

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/login - deve retornar 200 com token")
    void login_credenciaisValidas_retorna200() throws Exception {
        UserDTO.LoginRequest req = new UserDTO.LoginRequest();
        req.setEmail("leon@email.com");
        req.setPassword("senha123");

        when(authService.login(any())).thenReturn(respostaAuth());

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("token.mock"))
            .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/login - deve retornar 401 quando credenciais invalidas")
    void login_credenciaisInvalidas_retorna401() throws Exception {
        UserDTO.LoginRequest req = new UserDTO.LoginRequest();
        req.setEmail("leon@email.com");
        req.setPassword("senhaerrada");

        when(authService.login(any()))
            .thenThrow(new BadCredentialsException("Credenciais invalidas"));

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isUnauthorized());
    }
}
