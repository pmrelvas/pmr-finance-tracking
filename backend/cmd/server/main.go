package main

import (
	"context"
	"log"
	"net/http"
	"time"

	"pmr-finance-tracking-backend/internal/app"
	"pmr-finance-tracking-backend/internal/config"
	httphandler "pmr-finance-tracking-backend/internal/handler/http"
	"pmr-finance-tracking-backend/internal/infrastructure/db/mongodb"
	"pmr-finance-tracking-backend/internal/usecase/category"
)

func main() {
	cfg := config.Load()

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	client, err := mongodb.Connect(ctx, cfg.MongoURI)
	if err != nil {
		log.Fatalf("failed to connect to mongodb: %v", err)
	}

	db := client.Database("finance_tracker")

	// Infrastructure
	repo := mongodb.NewCategoryRepository(db)

	// Use Cases
	createUC := category.NewCreateCategoryUseCase(repo)
	readUC := category.NewReadCategoryUseCase(repo)
	updateUC := category.NewUpdateCategoryUseCase(repo)
	deleteUC := category.NewDeleteCategoryUseCase(repo)

	// Handler
	handler := httphandler.NewCategoryHandler(createUC, readUC, updateUC, deleteUC)

	addr := ":" + cfg.Port
	server := app.NewHTTPServer(addr, handler)

	log.Printf("starting HTTP server on %s", addr)

	if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
		log.Fatalf("server error: %v", err)
	}
}
