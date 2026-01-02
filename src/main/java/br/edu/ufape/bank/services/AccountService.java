package br.edu.ufape.bank.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import br.edu.ufape.bank.dto.responses.AccountResponseDTO;
import br.edu.ufape.bank.dto.requests.AccountRequestDTO;
import br.edu.ufape.bank.exceptions.ResourceNotFoundException;
import br.edu.ufape.bank.exceptions.UnprocessableEntityException;
import br.edu.ufape.bank.model.Account;
import br.edu.ufape.bank.model.User;
import br.edu.ufape.bank.repository.AccountRepository;

@Service
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final UserService userService;



    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO request, Authentication authentication) {

        User user = userService.getAuthenticatedUser(authentication);

        Account newAccount = new Account();
        newAccount.setAccountType(request.accountType());
        newAccount.setBalance(BigDecimal.ZERO);

        newAccount.setUser(user);

        Account savedAccount = accountRepository.save(newAccount);

        return new AccountResponseDTO(savedAccount);
    }

    public AccountResponseDTO findAccountById(Long accountId, Authentication authentication) {

        User user = userService.getAuthenticatedUser(authentication);

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Conta não encontrada")); 

        if (!account.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Acesso negado. Esta conta não pertence a você.");
        }

        return new AccountResponseDTO(account);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%010d", (long) (Math.random() * 1_000_000_0000L));
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());
        return accountNumber;
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    public void deleteAccount(Long id, Authentication authentication) {

        User user = userService.getAuthenticatedUser(authentication);

        Account account = getAccountById(id); 

        if (!account.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Acesso negado. Esta conta não pertence a você.");
        }

        accountRepository.delete(account);
    }
    
    public AccountResponseDTO updateAccount(Long id, AccountRequestDTO request, Authentication authentication) {
        
        User user = userService.getAuthenticatedUser(authentication);

        Account existingAccount = getAccountById(id);
        
        if (!existingAccount.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Acesso negado. Esta conta não pertence a você.");
        }
        
        existingAccount.setAccountType(request.accountType());
        
        Account savedAccount = accountRepository.save(existingAccount);
        return new AccountResponseDTO(savedAccount);
    }

    public Account deposit(Long accountId, BigDecimal amount, Authentication authentication) {
        
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UnprocessableEntityException("Deposit amount must be greater than zero.");
        }

        User user = userService.getAuthenticatedUser(authentication);

        Account account = getAccountById(accountId);

        if (!account.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Acesso negado. Esta conta não pertence a você.");
        }

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        return accountRepository.save(account);
    }
}