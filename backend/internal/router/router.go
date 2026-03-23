package router

import (
	"net/http"

	httphandler "pmr-finance-tracking-backend/internal/handler/http"
)

func NewRouter(categoryHandler *httphandler.CategoryHandler) http.Handler {
	mux := http.NewServeMux()

	mux.HandleFunc("/api/v1/hello", httphandler.HelloHandler)

	mux.HandleFunc("POST /api/v1/categories", categoryHandler.Create)
	mux.HandleFunc("GET /api/v1/categories", categoryHandler.GetAll)
	mux.HandleFunc("GET /api/v1/categories/{id}", categoryHandler.GetByID)
	mux.HandleFunc("PUT /api/v1/categories/{id}", categoryHandler.Update)
	mux.HandleFunc("DELETE /api/v1/categories/{id}", categoryHandler.Delete)

	return mux
}
