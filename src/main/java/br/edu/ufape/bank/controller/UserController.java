package br.edu.ufape.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import br.edu.ufape.bank.dto.responses.UserResponseDTO;
import br.edu.ufape.bank.dto.requests.UserRequestDTO;
import br.edu.ufape.bank.exceptions.ResourceNotFoundException;
import br.edu.ufape.bank.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String cpf = jwt.getClaimAsString("cpf"); 

        UserResponseDTO newUser = userService.createUser(keycloakId, name, email, cpf);

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO request,
            Authentication authentication
    ) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, request, authentication);
            return ResponseEntity.ok(updatedUser);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(Authentication authentication) {
        return userService.findAuthenticatedUser(authentication)
            .map(UserResponseDTO::new) // Sintaxe mais limpa (Reference Method)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}