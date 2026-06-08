package edu_gaj.pasir_gaj_weronika.service;

import edu_gaj.pasir_gaj_weronika.dto.BalanceDTO;
import edu_gaj.pasir_gaj_weronika.dto.TransactionDTO;
import edu_gaj.pasir_gaj_weronika.model.Transaction;
import edu_gaj.pasir_gaj_weronika.model.TransactionType;
import edu_gaj.pasir_gaj_weronika.model.User;
import edu_gaj.pasir_gaj_weronika.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public TransactionService(TransactionRepository transactionRepository,
                              CurrentUserService currentUserService) {
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    public List<Transaction> getAllTransactions() {
        User user = currentUserService.getCurrentUser();
        return transactionRepository.findAllByUser(user);
    }

    public Transaction getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono transakcji o ID: " + id));

        if (!transaction.getUser().getEmail().equals(currentUserService.getCurrentUser().getEmail())) {
            throw new RuntimeException("Nie masz dostepu do tej transakcji");
        }

        return transaction;
    }

    public Transaction createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();

        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(TransactionType.valueOf(transactionDTO.getType()));
        transaction.setTags(transactionDTO.getTags());
        transaction.setNotes(transactionDTO.getNotes());
        transaction.setUser(currentUserService.getCurrentUser());
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = getTransactionById(id);

        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(TransactionType.valueOf(transactionDTO.getType()));
        transaction.setTags(transactionDTO.getTags());
        transaction.setNotes(transactionDTO.getNotes());

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = getTransactionById(id);
        transactionRepository.delete(transaction);
    }

    public BalanceDTO getUserBalance(User user, Double days) {
        List<Transaction> userTransactions;

        if (days != null) {
            LocalDateTime fromDate = LocalDateTime.now().minusDays(days.longValue());
            userTransactions = transactionRepository.findAllByUserAndTimestampGreaterThanEqual(user, fromDate);
        } else {
            userTransactions = transactionRepository.findByUser(user);
        }

        double income = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        return new BalanceDTO(income, expense, income - expense);
    }
}