package edu_gaj.pasir_gaj_weronika.service;

import edu_gaj.pasir_gaj_weronika.dto.DebtDTO;
import edu_gaj.pasir_gaj_weronika.model.Debt;
import edu_gaj.pasir_gaj_weronika.model.Group;
import edu_gaj.pasir_gaj_weronika.model.User;
import edu_gaj.pasir_gaj_weronika.repository.DebtRepository;
import edu_gaj.pasir_gaj_weronika.repository.GroupRepository;
import edu_gaj.pasir_gaj_weronika.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebtService {

    private final DebtRepository debtRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final MembershipService membershipService;

    public DebtService(DebtRepository debtRepository,
                       GroupRepository groupRepository,
                       UserRepository userRepository,
                       CurrentUserService currentUserService,
                       MembershipService membershipService) {
        this.debtRepository = debtRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.membershipService = membershipService;
    }

    public List<Debt> getGroupDebts(Long groupId) {
        membershipService.assertCurrentUserIsGroupMember(groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy"));

        return debtRepository.findByGroup(group);
    }

    public Debt createDebt(DebtDTO dto) {
        membershipService.assertCurrentUserIsGroupMember(dto.getGroupId());

        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy"));

        User debtor = userRepository.findById(dto.getDebtorId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono dluznika"));

        User creditor = userRepository.findById(dto.getCreditorId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono wierzyciela"));

        if (debtor.getId().equals(creditor.getId())) {
            throw new RuntimeException("Dluznik i wierzyciel nie moga byc ta sama osoba");
        }

        membershipService.assertUserIsGroupMember(group.getId(), debtor.getId());
        membershipService.assertUserIsGroupMember(group.getId(), creditor.getId());

        Debt debt = new Debt();
        debt.setGroup(group);
        debt.setDebtor(debtor);
        debt.setCreditor(creditor);
        debt.setAmount(dto.getAmount());
        debt.setTitle(dto.getTitle());

        return debtRepository.save(debt);
    }

    public void deleteDebt(Long debtId) {
        Debt debt = getDebtForCurrentGroupMember(debtId);
        User currentUser = currentUserService.getCurrentUser();

        boolean isOwner = debt.getGroup().getOwner().getId().equals(currentUser.getId());
        boolean isDebtor = debt.getDebtor().getId().equals(currentUser.getId());
        boolean isCreditor = debt.getCreditor().getId().equals(currentUser.getId());

        if (!isOwner && !isDebtor && !isCreditor) {
            throw new RuntimeException("Nie mozesz usunac tego dlugu");
        }

        debtRepository.delete(debt);
    }

    public Debt markDebtAsPaid(Long debtId) {
        Debt debt = getDebtForCurrentGroupMember(debtId);
        User currentUser = currentUserService.getCurrentUser();

        if (!debt.getDebtor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Tylko dluznik moze oznaczyc dlug jako oplacony");
        }

        debt.setPaidByDebtor(true);
        debt.setConfirmedByCreditor(false);

        return debtRepository.save(debt);
    }

    public Debt confirmDebtPayment(Long debtId) {
        Debt debt = getDebtForCurrentGroupMember(debtId);
        User currentUser = currentUserService.getCurrentUser();

        if (!debt.getCreditor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Tylko wierzyciel moze potwierdzic splate dlugu");
        }

        if (!debt.isPaidByDebtor()) {
            throw new RuntimeException("Dlug musi zostac najpierw oznaczony jako oplacony przez dluznika");
        }

        debt.setConfirmedByCreditor(true);

        return debtRepository.save(debt);
    }

    private Debt getDebtForCurrentGroupMember(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono dlugu o ID " + debtId));

        membershipService.assertCurrentUserIsGroupMember(debt.getGroup().getId());

        return debt;
    }
}