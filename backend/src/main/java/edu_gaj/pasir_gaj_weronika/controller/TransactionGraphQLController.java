package edu_gaj.pasir_gaj_weronika.controller;

import edu_gaj.pasir_gaj_weronika.dto.BalanceDTO;
import edu_gaj.pasir_gaj_weronika.dto.TransactionDTO;
import edu_gaj.pasir_gaj_weronika.model.Transaction;
import edu_gaj.pasir_gaj_weronika.model.User;
import edu_gaj.pasir_gaj_weronika.service.CurrentUserService;
import edu_gaj.pasir_gaj_weronika.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TransactionGraphQLController {

    private final TransactionService transactionService;
    private final CurrentUserService currentUserService;

    public TransactionGraphQLController(TransactionService transactionService,
                                        CurrentUserService currentUserService) {
        this.transactionService = transactionService;
        this.currentUserService = currentUserService;
    }

    @QueryMapping
    public List<Transaction> transactions() {
        return transactionService.getAllTransactions();
    }

    @MutationMapping
    public Transaction addTransaction(@Valid @Argument TransactionDTO transactionDTO) {
        return transactionService.createTransaction(transactionDTO);
    }

    @MutationMapping
    public Transaction updateTransaction(@Argument Long id,
                                         @Valid @Argument TransactionDTO transactionDTO) {
        return transactionService.updateTransaction(id, transactionDTO);
    }

    @MutationMapping
    public Boolean deleteTransaction(@Argument Long id) {
        transactionService.deleteTransaction(id);
        return true;
    }

    @QueryMapping
    public BalanceDTO userBalance(@Argument Double days) {
        User user = currentUserService.getCurrentUser();
        return transactionService.getUserBalance(user, days);
    }
}