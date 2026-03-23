package http

import (
	"pmr-finance-tracking-backend/internal/domain/entity"
	"time"
)

type CreateCategoryRequest struct {
	Code        string `json:"code" validate:"required"`
	DisplayName string `json:"displayName" validate:"required"`
}

type UpdateCategoryRequest struct {
	Code        string `json:"code" validate:"required"`
	DisplayName string `json:"displayName" validate:"required"`
}

type CategoryResponse struct {
	ID          string    `json:"id"`
	Code        string    `json:"code"`
	DisplayName string    `json:"displayName"`
	CreatedAt   time.Time `json:"createdAt"`
	UpdatedAt   time.Time `json:"updatedAt"`
}

func ToCategoryResponse(c *entity.Category) CategoryResponse {
	return CategoryResponse{
		ID:          c.ID,
		Code:        c.Code,
		DisplayName: c.DisplayName,
		CreatedAt:   c.CreatedAt,
		UpdatedAt:   c.UpdatedAt,
	}
}

func ToCategoryListResponse(categories []*entity.Category) []CategoryResponse {
	res := make([]CategoryResponse, len(categories))
	for i, c := range categories {
		res[i] = ToCategoryResponse(c)
	}
	return res
}
