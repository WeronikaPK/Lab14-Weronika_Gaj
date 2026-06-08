package edu_gaj.pasir_gaj_weronika.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipDTO {

    @NotBlank(message = "Email uzytkownika nie moze byc pusty")
    @Email(message = "Email uzytkownika musi byc poprawnym adresem email")
    private String userEmail;

    @NotNull(message = "Id grupy nie moze byc puste")
    private Long groupId;
}