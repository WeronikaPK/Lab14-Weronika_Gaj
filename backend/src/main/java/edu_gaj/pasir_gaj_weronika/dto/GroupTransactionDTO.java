package edu_gaj.pasir_gaj_weronika.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupTransactionDTO {

    @NotNull(message = "Id grupy nie moze byc puste")
    private Long groupId;

    @NotNull(message = "Kwota nie moze byc pusta")
    @Positive(message = "Kwota musi byc wieksza od zera")
    private Double amount;

    @NotBlank(message = "Typ transakcji nie moze byc pusty")
    @Pattern(regexp = "INCOME|EXPENSE", message = "Typ musi miec wartosc INCOME albo EXPENSE")
    private String type;

    @NotBlank(message = "Tytul nie moze byc pusty")
    @Size(max = 100, message = "Tytul nie moze przekraczac 100 znakow")
    private String title;

    private List<Long> selectedUserIds;
}