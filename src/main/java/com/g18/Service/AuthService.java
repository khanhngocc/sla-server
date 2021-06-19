package com.g18.Service;

import com.g18.dto.RegisterRequest;
import com.g18.entity.Account;
import com.g18.entity.User;
import com.g18.entity.VerificationToken;
import com.g18.exceptions.AccountException;
import com.g18.exceptions.SLAException;
import com.g18.model.ApiError;
import com.g18.model.NotificationEmail;
import com.g18.repository.AccountRepository;
import com.g18.repository.UserRepository;
import com.g18.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;

    private final MailService mailService;

    @Transactional
    public void signup( RegisterRequest registerRequest) {
        if (checkEmailExistence(registerRequest.getEmail()) && checkUsernameExistence(registerRequest.getUsername())) {
            throw new AccountException("Email and username are already taken");
        }else if(checkEmailExistence(registerRequest.getEmail())){
            throw new AccountException("Email is already taken");
        } else if(checkUsernameExistence(registerRequest.getUsername())) {
            throw new AccountException("Username is already taken");
        } else {
            User user = new User();
            Account account = new Account();

            user.setEmail(registerRequest.getEmail());

            account.setUsername(registerRequest.getUsername());
            account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            account.setActive(false);
            account.setUser(user);
            account.setCreatedDate(Instant.now());
            account.setUpdateDate(Instant.now());

            userRepository.save(user);
            accountRepository.save(account);
            String token = generateVerificationToken(account);

            //send mail for
            mailService.sendMail(new NotificationEmail("Please Active your Account", user.getEmail(),
                    "Thank you for signing up SLS, " +
                            "please click on the below url to activate your account: " +
                            "http://localhost:8080/api/auth/accountVerification/" + token));
        }
    }

    private boolean checkEmailExistence(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    private boolean checkUsernameExistence(String username){
        Optional<Account> account = accountRepository.findByUsername(username);
        return account.isPresent();
    }

    private String generateVerificationToken(Account account) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setAccount(account);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Transactional
    public void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getAccount().getUsername();
        Account account = accountRepository.findByUsername(username).orElseThrow(() -> new AccountException("Account not found with name - " + username));
        account.setActive(true);
        accountRepository.save(account);
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new SLAException("Invalid Token")));
    }

}
