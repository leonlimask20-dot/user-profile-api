package com.leonlima.userapi.service;

import com.leonlima.userapi.dto.UserDTO;
import com.leonlima.userapi.model.User;
import com.leonlima.userapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO.ProfileResponse getProfile(Long id, String requestingEmail) {
        User target = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));

        User requester = userRepository.findByEmail(requestingEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuário solicitante não encontrado"));

        // Usuário comum só acessa o próprio perfil; ADMIN acessa qualquer um
        if (!requester.getId().equals(id) && requester.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Acesso negado ao perfil solicitado");
        }

        return UserDTO.ProfileResponse.fromEntity(target);
    }

    public UserDTO.ProfileResponse updateProfile(Long id, UserDTO.UpdateRequest request, String requestingEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));

        User requester = userRepository.findByEmail(requestingEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuário solicitante não encontrado"));

        if (!requester.getId().equals(id) && requester.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Acesso negado ao perfil solicitado");
        }

        // Atualização parcial: só sobrescreve os campos enviados na requisição
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getBio() != null) user.setBio(request.getBio());

        return UserDTO.ProfileResponse.fromEntity(userRepository.save(user));
    }

    public void deleteUser(Long id, String requestingEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));

        User requester = userRepository.findByEmail(requestingEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuário solicitante não encontrado"));

        if (!requester.getId().equals(id) && requester.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Acesso negado ao perfil solicitado");
        }

        userRepository.delete(user);
    }

    public List<UserDTO.ProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO.ProfileResponse::fromEntity)
                .toList();
    }
}
