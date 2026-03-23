package category

import (
	"context"
	"pmr-finance-tracking-backend/internal/domain/entity"
	"pmr-finance-tracking-backend/internal/domain/repository"
)

type readCategoryUseCase struct {
	repo repository.CategoryRepository
}

func NewReadCategoryUseCase(repo repository.CategoryRepository) ReadCategoryUseCase {
	return &readCategoryUseCase{repo: repo}
}

func (u *readCategoryUseCase) ExecuteGetAll(ctx context.Context) ([]*entity.Category, error) {
	return u.repo.GetAll(ctx)
}

func (u *readCategoryUseCase) ExecuteGetByID(ctx context.Context, id string) (*entity.Category, error) {
	return u.repo.GetByID(ctx, id)
}
