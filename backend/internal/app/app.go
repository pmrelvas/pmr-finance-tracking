package app

import (
	"net/http"
	httphandler "pmr-finance-tracking-backend/internal/handler/http"
	routerpkg "pmr-finance-tracking-backend/internal/router"
)

func NewHTTPServer(addr string, categoryHandler *httphandler.CategoryHandler) *http.Server {
	r := routerpkg.NewRouter(categoryHandler)

	server := &http.Server{
		Addr:    addr,
		Handler: r,
	}

	return server
}

