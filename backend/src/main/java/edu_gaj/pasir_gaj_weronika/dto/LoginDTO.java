package edu_gaj.pasir_gaj_weronika.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {

    @Email(message = "Podaj poprawny adres e-mail")
    @NotBlank(message = "Adres e-mail jest wymagany")
    private String email;

    @NotBlank(message = "Haslo jest wymagane")
    private String password;
}