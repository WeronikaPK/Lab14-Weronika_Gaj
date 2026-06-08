package edu_gaj.pasir_gaj_weronika.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDTO {

    private double totalIncome;
    private double totalExpense;
    private double balance;
}