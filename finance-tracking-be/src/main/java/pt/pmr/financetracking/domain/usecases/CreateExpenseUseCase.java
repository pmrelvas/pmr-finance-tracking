package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.ExpenseRepository;

@ApplicationScoped
@RequiredArgsConstructor
public class CreateExpenseUseCase {

    private final ExpenseRepository expenseRepository;
    private final ReadCategoryUseCase readCategoryUseCase;

    public Uni<Expense> execute(Expense expense) {
        return readCategoryUseCase.executeFindById(expense.category().id())
                .flatMap(category -> {
                    SubCategory subCategory = resolveSubCategory(expense, category);
                    Expense fullExpense = expense.toBuilder()
                            .category(category)
                            .subCategory(subCategory)
                            .build();
                    return expenseRepository.create(fullExpense);
                });
    }

    private SubCategory resolveSubCategory(Expense expense, Category category) {
        if (expense.subCategory() == null) {
            return null;
        }
        String subCategoryId = expense.subCategory().id();
        return category.subCategories().stream()
                .filter(sc -> sc.id().equals(subCategoryId))
                .findFirst()
                .orElseThrow(() -> EntityNotFoundException.buildForId(SubCategory.class, subCategoryId));
    }
}
