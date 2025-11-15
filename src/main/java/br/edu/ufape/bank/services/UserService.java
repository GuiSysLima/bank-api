package br.edu.ufape.bank.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import br.edu.ufape.bank.dto.reponses.UserResponseDTO;
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
        // 1. Pega o usuário logado (do token)
        User userFromToken = getAuthenticatedUser(authentication);

        // 2. Busca o usuário que ele quer editar (pelo ID da URL)
        User userToUpdate = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        // 3. A VERIFICAÇÃO DE SEGURANÇA MAIS IMPORTANTE:
        // O usuário do token é o mesmo usuário que ele está tentando editar?
        if (!userFromToken.getId().equals(userToUpdate.getId())) {
            // Se não for, é uma tentativa de editar o perfil de outra pessoa.
            throw new SecurityException("Acesso negado. Você só pode editar seu próprio perfil.");
        }

        // 4. Se ele é o dono, atualize os campos permitidos.
        // O que pode ser editado? Provavelmente só o 'name'.
        // 'email' e 'cpf' são sensíveis e atrelados ao Keycloak.
        // Vamos atualizar apenas o nome por enquanto:
        userToUpdate.setName(request.name());
        
        // (Nota: Se você reusar o UserRequestDTO, ele terá email e cpf. 
        // Certifique-se de que sua lógica não permite sobrescrever dados críticos).

        User savedUser = userRepository.save(userToUpdate);
        return new UserResponseDTO(savedUser);
    }

    // CRIE ESTE MÉTODO HELPER
    public User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Usuário não autenticado.");
        }
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getSubject();

        return userRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado no banco de dados "));
    }

    @Transactional
    public UserResponseDTO findUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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
