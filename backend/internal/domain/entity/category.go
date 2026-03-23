package entity

import "time"

type Category struct {
	ID          string
	Code        string
	DisplayName string
	CreatedAt   time.Time
	UpdatedAt   time.Time
}
