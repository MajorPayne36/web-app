package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.User;
import org.example.app.dto.*;
import org.example.app.exception.*;
import org.example.app.jpa.JpaTransactionTemplate;
import org.example.app.repository.UserRepository;
import org.example.framework.security.*;
import org.example.framework.util.KeyValue;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class UserService implements AuthenticationProvider, AnonymousProvider, BasicAuthenticationProvider {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final StringKeyGenerator keyGenerator;

    @Override
    public Authentication authenticate(Authentication authentication) {
        final String token = (String) authentication.getPrincipal();
        return repository.findByToken(token)
                // TODO: add user roles
                .map(o -> new TokenAuthentication(o, null, List.of(), true))
                .orElseThrow(AuthenticationException::new);

    }

    @Override
    public Authentication baseAuthenticate(Authentication authentication) throws AuthenticationException {
        final String username = (String) authentication.getPrincipal();
        final String password = (String) authentication.getCredentials();
        return repository.findByUsernameAndPassword(username, password)
                .map(o -> new BasicAuthentication(o, null, List.of(), true))
                .orElseThrow(AuthenticationException::new);
    }

    @Override
    public AnonymousAuthentication provide() {
        return new AnonymousAuthentication(new User(
                -1,
                "anonymous"
        ));
    }

    public RegistrationResponseDto register(RegistrationRequestDto requestDto) {
        // TODO login:
        //  case-sensitivity: coursar Coursar
        //  cleaning: "  Coursar   "
        //  allowed symbols: [A-Za-z0-9]{2,60}
        //  mis...: Admin, Support, root, ...
        //  мат: ...
        // FIXME: check for nullability
        final var username = requestDto.getUsername().trim().toLowerCase();
        // TODO password:
        //  min-length: 8
        //  max-length: 64
        //  non-dictionary
        final var password = requestDto.getPassword().trim();
        final var hash = passwordEncoder.encode(password);
        final var token = keyGenerator.generateKey();
        final var saved = repository.save(0, username, hash).orElseThrow(RegistrationException::new);

        repository.saveToken(saved.getId(), token);
        return new RegistrationResponseDto(saved.getId(), saved.getUsername(), token);
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        final var username = requestDto.getUsername().trim().toLowerCase();
        final var password = requestDto.getPassword().trim();

        final var saved = repository.getByUsernameWithPassword(username)
                .orElseThrow(UserNotFoundException::new);

        // TODO: be careful - slow
        if (!passwordEncoder.matches(password, saved.getPassword())) {
            // FIXME: Security issue
            throw new PasswordNotMatchesException();
        }

        final var token = keyGenerator.generateKey();
        repository.saveToken(saved.getId(), token);

        return new LoginResponseDto(saved.getId(), saved.getUsername(), token);
    }

    public int reset(User user, PassResetDto dto) {
        final var username = user.getUsername().trim().toLowerCase();
        final var password = dto.getNewPassword().trim();
        final var encodedPassword = passwordEncoder.encode(password);

        return repository.reset(username, encodedPassword);
    }

    public int confirmReset(PassResetConfirmDto confirmDto) {
        AtomicInteger result = new AtomicInteger();
        repository.findByCode(confirmDto.getCode()).ifPresentOrElse(
                v -> {
                    if (v.isActive()) {
                        result.set(repository.confirmReset(confirmDto.getUsername(), confirmDto.getCode()));
                    } else {
                        throw new CardNotActiveException("{\"status\" : \"error\", \"message\" : \"{\"status\" : \"error\", \"message\" : \"Your card is not ACTIVE!\"}");
                    }
                },
                () -> {
                    throw new UnsupportedResetConfirmException("{\"status\" : \"error\", \"message\" : \"{\"status\" : \"error\", \"message\" : \"Cant confirm because code was wrong!\"}");
                }
        );
        return result.get();
    }
}
