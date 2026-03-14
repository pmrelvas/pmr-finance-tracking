package router

import (
	"net/http"

	httphandler "pmr-finance-tracking-backend/internal/handler/http"
)

func NewRouter() http.Handler {
	mux := http.NewServeMux()

	mux.HandleFunc("/api/v1/hello", httphandler.HelloHandler)

	return mux
}

