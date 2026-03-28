package com.leonlima.userapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonlima.userapi.dto.UserDTO;
import com.leonlima.userapi.security.JwtAuthFilter;
import com.leonlima.userapi.security.JwtService;
import com.leonlima.userapi.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = UserController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthFilter.class
    )
)
@DisplayName("UserController - testes MockMvc")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private UserDTO.ProfileResponse perfil() {
        return UserDTO.ProfileResponse.builder()
            .id(1L)
            .name("Leon Lima")
            .email("leon@email.com")
            .role("USER")
            .build();
    }

    @Test
    @WithMockUser(username = "leon@email.com", roles = "USER")
    @DisplayName("GET /api/users/{id} - deve retornar perfil do usuario")
    void getProfile_autenticado_retornaPerfil() throws Exception {
        when(userService.getProfile(1L, "leon@email.com")).thenReturn(perfil());

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("leon@email.com"));
    }

    @Test
    @WithMockUser(username = "outro@email.com", roles = "USER")
    @DisplayName("GET /api/users/{id} - deve retornar 5xx quando acesso negado pelo servico")
    void getProfile_acessoNegado_retornaErro() throws Exception {
        when(userService.getProfile(1L, "outro@email.com"))
            .thenThrow(new AccessDeniedException("Acesso negado"));

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "leon@email.com", roles = "USER")
    @DisplayName("GET /api/users/{id} - deve retornar 404 quando nao encontrado")
    void getProfile_naoEncontrado_retorna404() throws Exception {
        when(userService.getProfile(99L, "leon@email.com"))
            .thenThrow(new EntityNotFoundException("Usuario nao encontrado"));

        mockMvc.perform(get("/api/users/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "leon@email.com", roles = "USER")
    @DisplayName("PUT /api/users/{id} - deve atualizar perfil com sucesso")
    void updateProfile_dadosValidos_retornaPerfil() throws Exception {
        UserDTO.UpdateRequest req = new UserDTO.UpdateRequest();
        req.setName("Leon Atualizado");

        when(userService.updateProfile(eq(1L), any(), eq("leon@email.com")))
            .thenReturn(perfil());

        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "leon@email.com", roles = "USER")
    @DisplayName("DELETE /api/users/{id} - deve deletar usuario com sucesso")
    void deleteUser_autenticado_retorna204() throws Exception {
        doNothing().when(userService).deleteUser(1L, "leon@email.com");

        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = "ADMIN")
    @DisplayName("GET /api/users - deve listar todos os usuarios (admin)")
    void getAllUsers_admin_retornaLista() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(perfil()));

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/users/{id} - deve retornar 401 quando nao autenticado")
    void getProfile_semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isUnauthorized());
    }
}
