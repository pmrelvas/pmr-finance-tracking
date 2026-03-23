package category

import (
	"context"
	"pmr-finance-tracking-backend/internal/domain/entity"
	"pmr-finance-tracking-backend/internal/domain/repository"
	"time"
)

type updateCategoryUseCase struct {
	repo repository.CategoryRepository
}

func NewUpdateCategoryUseCase(repo repository.CategoryRepository) UpdateCategoryUseCase {
	return &updateCategoryUseCase{repo: repo}
}

func (u *updateCategoryUseCase) Execute(ctx context.Context, category *entity.Category) error {
	if category.Code == "" {
		return ErrEmptyCode
	}
	if category.DisplayName == "" {
		return ErrEmptyDisplayName
	}

	category.UpdatedAt = time.Now()
	return u.repo.Update(ctx, category)
}
