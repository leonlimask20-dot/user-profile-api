package com.leonlima.userapi.controller;

import com.leonlima.userapi.dto.UserDTO;
import com.leonlima.userapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Operações de perfil — requerem token JWT")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar perfil", description = "Usuário comum só acessa o próprio perfil. ADMIN acessa qualquer um.")
    public ResponseEntity<UserDTO.ProfileResponse> getProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.getProfile(id, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar perfil", description = "Atualização parcial — apenas os campos enviados são modificados.")
    public ResponseEntity<UserDTO.ProfileResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO.UpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.updateProfile(id, request, userDetails.getUsername()));
    }

    // DELETE retorna 204 No Content — sem corpo na resposta, conforme convenção REST
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover conta")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.deleteUser(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Restrito a ADMIN.")
    public ResponseEntity<List<UserDTO.ProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
