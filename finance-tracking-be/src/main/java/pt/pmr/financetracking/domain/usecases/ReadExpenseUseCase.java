package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.ExpenseFilter;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.ExpenseRepository;

@ApplicationScoped
@RequiredArgsConstructor
public class ReadExpenseUseCase {

    private final ExpenseRepository expenseRepository;

    public Multi<Expense> executeFindAll(ExpenseFilter filter) {
        return expenseRepository.fetchAll(filter);
    }

    public Uni<Expense> executeFindById(String id) {
        return expenseRepository.fetchById(id)
                .map(opt -> opt.orElseThrow(() -> EntityNotFoundException.buildForId(Expense.class, id)));
    }
}
