package sev.amorlov.plant_nursery.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Отключаем CSRF, так как мы используем JWT-токены, а не сессионные куки
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Настраиваем правила доступа к эндпоинтам (Матрица доступа)
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем вход и регистрацию всем без авторизации
                        .requestMatchers("/api/auth/**").permitAll()

                        // Просмотр каталога растений доступен всем
                        .requestMatchers(HttpMethod.GET, "/api/plants/**").permitAll()

                        // Управление растениями (создание, удаление) — только для АДМИНА
                        .requestMatchers("/api/plants/**").hasRole("ADMIN")

                        // Управление поставщиками — МЕНЕДЖЕР или АДМИН
                        .requestMatchers("/api/suppliers/**").hasAnyRole("MANAGER", "ADMIN")

                        // Заказы: создавать может любой авторизованный, отменять — МЕНЕДЖЕР или АДМИН
                        .requestMatchers(HttpMethod.POST, "/api/orders/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/*/cancel").hasAnyRole("MANAGER", "ADMIN")

                        // Все остальные запросы по умолчанию требуют авторизации
                        .anyRequest().authenticated()
                )

                // 3. Переводим сессии в режим Stateless (сервер ничего не помнит)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Добавляем наш JWT фильтр прямо перед стандартным фильтром аутентификации Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Бин для шифрования паролей (будет использоваться при регистрации и логине)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}