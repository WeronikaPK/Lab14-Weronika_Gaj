package edu_gaj.pasir_gaj_weronika.service;

import edu_gaj.pasir_gaj_weronika.model.User;
import edu_gaj.pasir_gaj_weronika.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("Uzytkownik nie jest zalogowany");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zalogowanego uzytkownika: " + email));
    }
}