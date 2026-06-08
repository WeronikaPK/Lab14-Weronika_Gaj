package edu_gaj.pasir_gaj_weronika.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    @JsonAlias("login")
    @NotBlank(message = "Nazwa uzytkownika jest wymagana")
    private String username;

    @Email(message = "Podaj poprawny adres e-mail")
    @NotBlank(message = "Adres e-mail jest wymagany")
    private String email;

    @NotBlank(message = "Haslo nie moze byc puste")
    private String password;
}