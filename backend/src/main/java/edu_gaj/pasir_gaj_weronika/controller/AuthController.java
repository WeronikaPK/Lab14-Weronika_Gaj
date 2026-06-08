package edu_gaj.pasir_gaj_weronika.controller;

import edu_gaj.pasir_gaj_weronika.dto.LoginDTO;
import edu_gaj.pasir_gaj_weronika.dto.UserDTO;
import edu_gaj.pasir_gaj_weronika.model.User;
import edu_gaj.pasir_gaj_weronika.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping({"/api/register", "/register", "/api/auth/register"})
    public User register(@Valid @RequestBody UserDTO dto) {
        return userService.register(dto);
    }

    @PostMapping({"/api/login", "/login", "/api/auth/login"})
    public Map<String, String> login(@Valid @RequestBody LoginDTO dto) {
        String token = userService.login(dto);
        return Map.of("token", token);
    }
}