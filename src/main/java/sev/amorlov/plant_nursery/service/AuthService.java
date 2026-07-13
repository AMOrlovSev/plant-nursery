package sev.amorlov.plant_nursery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sev.amorlov.plant_nursery.dto.AuthResponseDto;
import sev.amorlov.plant_nursery.dto.LoginRequestDto;
import sev.amorlov.plant_nursery.model.UserEntity;
import sev.amorlov.plant_nursery.repository.UserRepository;
import sev.amorlov.plant_nursery.security.JwtService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDto login(LoginRequestDto dto) {
        log.info("Authentication attempt for user: {}", dto.email());

        // 1. Ищем пользователя в БД
        UserEntity user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("Неверный email или пароль"));

        // 2. Проверяем, активна ли учетная запись
        if (!user.getEnabled()) {
            throw new IllegalStateException("Учетная запись заблокирована");
        }


        //log.info(passwordEncoder.encode("admin123"));
        // 3. Проверяем хэш пароля
        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            log.warn("Invalid password provided for user: {}", dto.email());
            throw new IllegalArgumentException("Неверный email или пароль");
        }

        // 4. Генерируем JWT-токен
        String token = jwtService.generateToken(user);
        log.info("User {} successfully authenticated", dto.email());

        return new AuthResponseDto(token, user.getEmail(), user.getRole().name());
    }
}