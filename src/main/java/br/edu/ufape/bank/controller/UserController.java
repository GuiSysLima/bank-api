package br.edu.ufape.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;


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
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO request, 
            Authentication authentication 
    ) {

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String keycloakId = jwt.getSubject();
        String emailDoToken = jwt.getClaimAsString("email");
        UserResponseDTO newUser = userService.createUser(request, keycloakId);

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
            // Se o service lançar "Acesso negado"
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
