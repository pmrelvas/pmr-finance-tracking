package models

import (
	"pmr-finance-tracking-backend/internal/domain/entity"
	"time"
)

type CategoryModel struct {
	ID          string    `bson:"_id"`
	Code        string    `bson:"code"`
	DisplayName string    `bson:"displayName"`
	CreatedAt   time.Time `bson:"createdAt"`
	UpdatedAt   time.Time `bson:"updatedAt"`
}

func ToDomain(m *CategoryModel) *entity.Category {
	return &entity.Category{
		ID:          m.ID,
		Code:        m.Code,
		DisplayName: m.DisplayName,
		CreatedAt:   m.CreatedAt,
		UpdatedAt:   m.UpdatedAt,
	}
}

func FromDomain(e *entity.Category) *CategoryModel {
	return &CategoryModel{
		ID:          e.ID,
		Code:        e.Code,
		DisplayName: e.DisplayName,
		CreatedAt:   e.CreatedAt,
		UpdatedAt:   e.UpdatedAt,
	}
}
