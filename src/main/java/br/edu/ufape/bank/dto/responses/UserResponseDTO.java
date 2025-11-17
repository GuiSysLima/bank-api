package br.edu.ufape.bank.dto.responses;

import br.edu.ufape.bank.model.User;

public record UserResponseDTO(
    Long id,
    String name,
    String email,
    String cpf
) {
    public UserResponseDTO(User user) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCpf()
        );
    }
}