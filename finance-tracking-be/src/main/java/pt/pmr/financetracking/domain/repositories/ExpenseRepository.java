package pt.pmr.financetracking.domain.repositories;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.ExpenseFilter;

import java.util.Optional;

public interface ExpenseRepository {

    Multi<Expense> fetchAll(ExpenseFilter filter);

    Uni<Optional<Expense>> fetchById(String id);

    Uni<Expense> create(Expense expense);

    Uni<Optional<Expense>> update(String id, Expense expense);

    Uni<Long> deleteAll();
}
