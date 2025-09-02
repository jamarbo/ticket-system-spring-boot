package com.example.tickets.users;

import com.example.tickets.security.SecurityUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> me(@AuthenticationPrincipal Object principal) {
        if (principal instanceof SecurityUser su) {
            String email = su.getUsername();
            String name = userRepository.findByEmail(email).map(User::getName).orElse("");
            String role = su.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .map(r -> r.replaceFirst("^ROLE_", ""))
                    .orElse("");
            return ResponseEntity.ok(UserInfoDto.builder()
                    .email(email)
                    .name(name)
                    .role(role)
                    .build());
        }
        return ResponseEntity.ok(UserInfoDto.builder().email("").name("").role("").build());
    }
}
