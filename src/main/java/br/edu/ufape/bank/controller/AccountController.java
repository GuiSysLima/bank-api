package br.edu.ufape.bank.controller;

import br.edu.ufape.bank.dto.responses.AccountResponseDTO;
import br.edu.ufape.bank.dto.requests.AccountRequestDTO;
import br.edu.ufape.bank.dto.requests.AmountRequestDTO;
import br.edu.ufape.bank.exceptions.ResourceNotFoundException;
import br.edu.ufape.bank.services.AccountService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(
            @Valid @RequestBody AccountRequestDTO request,
            Authentication authentication 
    ) {
        AccountResponseDTO newAccount = accountService.createAccount(request, authentication);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> findAccountById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            AccountResponseDTO account = accountService.findAccountById(id, authentication);
            return ResponseEntity.ok(account);
        
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable Long id,
            @Valid @RequestBody AmountRequestDTO request,
            Authentication authentication
    ) {
        try {
            accountService.deposit(id, request.getAmount(), authentication);
            return ResponseEntity.ok().build(); 

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            accountService.deleteAccount(id, authentication);
            return ResponseEntity.noContent().build();

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequestDTO request,
            Authentication authentication
    ) {
        try {
           
            AccountResponseDTO updatedAccount = accountService.updateAccount(id, request, authentication);
            return ResponseEntity.ok(updatedAccount);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}