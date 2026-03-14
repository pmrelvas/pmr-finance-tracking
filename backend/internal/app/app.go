package app

import (
	"net/http"
	routerpkg "pmr-finance-tracking-backend/internal/router"
)

func NewHTTPServer(addr string) *http.Server {
	r := routerpkg.NewRouter()

	server := &http.Server{
		Addr:    addr,
		Handler: r,
	}

	return server
}

