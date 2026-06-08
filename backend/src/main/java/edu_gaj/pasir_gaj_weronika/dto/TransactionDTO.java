package edu_gaj.pasir_gaj_weronika.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO {

    @NotNull(message = "Kwota nie moze byc pusta")
    @DecimalMin(value = "0.01", message = "Kwota musi byc wieksza od 0")
    private Double amount;

    @NotNull(message = "Typ transakcji jest wymagany")
    @Pattern(regexp = "INCOME|EXPENSE", message = "Typ musi miec wartosc INCOME albo EXPENSE")
    private String type;

    @Size(max = 50, message = "Tagi nie moga przekraczac 50 znakow")
    private String tags;

    @Size(max = 255, message = "Notatka moze miec maksymalnie 255 znakow")
    private String notes;
}