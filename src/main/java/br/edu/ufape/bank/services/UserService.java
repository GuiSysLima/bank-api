package br.edu.ufape.bank.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Optional;

import br.edu.ufape.bank.dto.responses.UserResponseDTO;
import br.edu.ufape.bank.dto.requests.UserRequestDTO;
import br.edu.ufape.bank.exceptions.UnprocessableEntityException;
import br.edu.ufape.bank.exceptions.ResourceNotFoundException;
import br.edu.ufape.bank.model.User;
import br.edu.ufape.bank.repository.UserRepository;
import jakarta.transaction.Transactional;


@Service
public class UserService {
   
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request, String keycloakId) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UnprocessableEntityException("Email already in use: " + request.email());
        }

        User newUser = new User();
        newUser.setName(request.name());
        newUser.setEmail(request.email());
        newUser.setCpf(request.cpf());
        newUser.setKeycloakId(keycloakId);
        
        User savedUser = userRepository.save(newUser);

        return toResponseDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO request, Authentication authentication) {
        
        User userFromToken = getAuthenticatedUser(authentication);

        User userToUpdate = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        if (!userFromToken.getId().equals(userToUpdate.getId())) {
            throw new SecurityException("Acesso negado. Você só pode editar seu próprio perfil.");
        }

        userToUpdate.setName(request.name());
        
        User savedUser = userRepository.save(userToUpdate);
        return new UserResponseDTO(savedUser);
    }

    public Optional<User> findAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getSubject();
        
        return userRepository.findByKeycloakId(keycloakId); 
    }

    public User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Usuário não autenticado.");
        }
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getSubject();

        return userRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado no banco de dados"));
    }

    @Transactional
    public UserResponseDTO findUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return toResponseDTO(user);
    }

    public UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCpf()
        );
    }
}
