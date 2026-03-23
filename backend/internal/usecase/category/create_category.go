package category

import (
	"context"
	"errors"
	"pmr-finance-tracking-backend/internal/domain/entity"
	"pmr-finance-tracking-backend/internal/domain/repository"
	"time"
)

var (
	ErrEmptyCode        = errors.New("category code cannot be empty")
	ErrEmptyDisplayName = errors.New("category display name cannot be empty")
)

type createCategoryUseCase struct {
	repo repository.CategoryRepository
}

func NewCreateCategoryUseCase(repo repository.CategoryRepository) CreateCategoryUseCase {
	return &createCategoryUseCase{repo: repo}
}

func (u *createCategoryUseCase) Execute(ctx context.Context, category *entity.Category) error {
	if category.Code == "" {
		return ErrEmptyCode
	}
	if category.DisplayName == "" {
		return ErrEmptyDisplayName
	}

	category.CreatedAt = time.Now()
	category.UpdatedAt = category.CreatedAt
	return u.repo.Create(ctx, category)
}
