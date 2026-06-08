package edu_gaj.pasir_gaj_weronika.service;

import edu_gaj.pasir_gaj_weronika.dto.LoginDTO;
import edu_gaj.pasir_gaj_weronika.dto.UserDTO;
import edu_gaj.pasir_gaj_weronika.exception.UserAlreadyExistsException;
import edu_gaj.pasir_gaj_weronika.model.User;
import edu_gaj.pasir_gaj_weronika.repository.UserRepository;
import edu_gaj.pasir_gaj_weronika.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(UserDTO dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Uzytkownik z tym adresem e-mail juz istnieje");
        }

        User user = new User();

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    public String login(LoginDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono uzytkownika"));

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Nieprawidlowe dane logowania");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono uzytkownika: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}