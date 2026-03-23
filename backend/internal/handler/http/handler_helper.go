package http

import (
	"encoding/json"
	"net/http"

	"github.com/go-playground/validator/v10"
)

// DecodeAndValidate handles JSON decoding and struct validation in one step
func DecodeAndValidate[T any](w http.ResponseWriter, r *http.Request, v *validator.Validate) (T, bool) {
	var req T
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return req, false
	}

	if err := v.Struct(req); err != nil {
		http.Error(w, "Validation failed: "+err.Error(), http.StatusBadRequest)
		return req, false
	}

	return req, true
}

// WriteJSON is a helper to write consistent JSON responses
func WriteJSON(w http.ResponseWriter, status int, data any) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	if data != nil {
		_ = json.NewEncoder(w).Encode(data)
	}
}
