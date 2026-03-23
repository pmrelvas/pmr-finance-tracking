package category

import (
	"context"
	"pmr-finance-tracking-backend/internal/domain/repository"
)

type deleteCategoryUseCase struct {
	repo repository.CategoryRepository
}

func NewDeleteCategoryUseCase(repo repository.CategoryRepository) DeleteCategoryUseCase {
	return &deleteCategoryUseCase{repo: repo}
}

func (u *deleteCategoryUseCase) Execute(ctx context.Context, id string) error {
	return u.repo.Delete(ctx, id)
}
