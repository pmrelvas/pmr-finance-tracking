package mongodb

import (
	"context"
	"errors"
	"pmr-finance-tracking-backend/internal/domain/entity"
	"pmr-finance-tracking-backend/internal/domain/repository"
	"pmr-finance-tracking-backend/internal/infrastructure/db/mongodb/models"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
)

type categoryRepository struct {
	collection *mongo.Collection
}

func NewCategoryRepository(db *mongo.Database) repository.CategoryRepository {
	return &categoryRepository{
		collection: db.Collection("categories"),
	}
}

func (r *categoryRepository) Create(ctx context.Context, category *entity.Category) error {
	if category.ID == "" {
		category.ID = primitive.NewObjectID().Hex()
	}

	m := models.FromDomain(category)
	_, err := r.collection.InsertOne(ctx, m)
	return err
}

func (r *categoryRepository) GetByID(ctx context.Context, id string) (*entity.Category, error) {
	var m models.CategoryModel
	err := r.collection.FindOne(ctx, bson.M{"_id": id}).Decode(&m)
	if err != nil {
		if errors.Is(err, mongo.ErrNoDocuments) {
			return nil, errors.New("category not found")
		}
		return nil, err
	}
	return models.ToDomain(&m), nil
}

func (r *categoryRepository) GetAll(ctx context.Context) ([]*entity.Category, error) {
	cursor, err := r.collection.Find(ctx, bson.D{})
	if err != nil {
		return nil, err
	}
	defer cursor.Close(ctx)

	var mList []models.CategoryModel
	if err := cursor.All(ctx, &mList); err != nil {
		return nil, err
	}

	categories := make([]*entity.Category, len(mList))
	for i, m := range mList {
		categories[i] = models.ToDomain(&m)
	}
	return categories, nil
}

func (r *categoryRepository) Update(ctx context.Context, category *entity.Category) error {
	m := models.FromDomain(category)
	result, err := r.collection.ReplaceOne(ctx, bson.M{"_id": category.ID}, m)
	if err != nil {
		return err
	}
	if result.MatchedCount == 0 {
		return errors.New("category not found")
	}
	return nil
}

func (r *categoryRepository) Delete(ctx context.Context, id string) error {
	result, err := r.collection.DeleteOne(ctx, bson.M{"_id": id})
	if err != nil {
		return err
	}
	if result.DeletedCount == 0 {
		return errors.New("category not found")
	}
	return nil
}
