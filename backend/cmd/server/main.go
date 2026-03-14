package main

import (
	"log"
	"net/http"
	"os"

	"pmr-finance-tracking-backend/internal/app"
)

func main() {
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	addr := ":" + port
	server := app.NewHTTPServer(addr)

	log.Printf("starting HTTP server on %s", addr)

	if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
		log.Fatalf("server error: %v", err)
	}
}

