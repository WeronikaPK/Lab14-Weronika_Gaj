package edu_gaj.pasir_gaj_weronika.controller;

import edu_gaj.pasir_gaj_weronika.dto.GroupTransactionDTO;
import edu_gaj.pasir_gaj_weronika.service.GroupTransactionService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GroupTransactionGraphQLController {

    private final GroupTransactionService groupTransactionService;

    public GroupTransactionGraphQLController(GroupTransactionService groupTransactionService) {
        this.groupTransactionService = groupTransactionService;
    }

    @MutationMapping
    public Boolean addGroupTransaction(@Valid @Argument GroupTransactionDTO groupTransactionDTO) {
        return groupTransactionService.addGroupTransaction(groupTransactionDTO);
    }
}