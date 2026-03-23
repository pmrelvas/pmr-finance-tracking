package category

import (
	"context"
	"pmr-finance-tracking-backend/internal/domain/entity"
)

type CreateCategoryUseCase interface {
	Execute(ctx context.Context, category *entity.Category) error
}

type ReadCategoryUseCase interface {
	ExecuteGetAll(ctx context.Context) ([]*entity.Category, error)
	ExecuteGetByID(ctx context.Context, id string) (*entity.Category, error)
}

type UpdateCategoryUseCase interface {
	Execute(ctx context.Context, category *entity.Category) error
}

type DeleteCategoryUseCase interface {
	Execute(ctx context.Context, id string) error
}
