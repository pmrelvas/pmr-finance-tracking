package http

import (
	"net/http"
	"pmr-finance-tracking-backend/internal/domain/entity"
	"pmr-finance-tracking-backend/internal/usecase/category"

	"github.com/go-playground/validator/v10"
)

type CategoryHandler struct {
	createUC  category.CreateCategoryUseCase
	readUC    category.ReadCategoryUseCase
	updateUC  category.UpdateCategoryUseCase
	deleteUC  category.DeleteCategoryUseCase
	validator *validator.Validate
}

func NewCategoryHandler(
	createUC category.CreateCategoryUseCase,
	readUC category.ReadCategoryUseCase,
	updateUC category.UpdateCategoryUseCase,
	deleteUC category.DeleteCategoryUseCase,
) *CategoryHandler {
	return &CategoryHandler{
		createUC:  createUC,
		readUC:    readUC,
		updateUC:  updateUC,
		deleteUC:  deleteUC,
		validator: validator.New(),
	}
}

func (h *CategoryHandler) Create(w http.ResponseWriter, r *http.Request) {
	req, ok := DecodeAndValidate[CreateCategoryRequest](w, r, h.validator)
	if !ok {
		return
	}

	cat := &entity.Category{
		Code:        req.Code,
		DisplayName: req.DisplayName,
	}

	if err := h.createUC.Execute(r.Context(), cat); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	WriteJSON(w, http.StatusCreated, ToCategoryResponse(cat))
}

func (h *CategoryHandler) GetAll(w http.ResponseWriter, r *http.Request) {
	categories, err := h.readUC.ExecuteGetAll(r.Context())
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	WriteJSON(w, http.StatusOK, ToCategoryListResponse(categories))
}

func (h *CategoryHandler) GetByID(w http.ResponseWriter, r *http.Request) {
	id := r.PathValue("id")
	cat, err := h.readUC.ExecuteGetByID(r.Context(), id)
	if err != nil {
		http.Error(w, "Category not found", http.StatusNotFound)
		return
	}

	WriteJSON(w, http.StatusOK, ToCategoryResponse(cat))
}

func (h *CategoryHandler) Update(w http.ResponseWriter, r *http.Request) {
	id := r.PathValue("id")
	req, ok := DecodeAndValidate[UpdateCategoryRequest](w, r, h.validator)
	if !ok {
		return
	}

	cat := &entity.Category{
		ID:          id,
		Code:        req.Code,
		DisplayName: req.DisplayName,
	}

	if err := h.updateUC.Execute(r.Context(), cat); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	WriteJSON(w, http.StatusOK, ToCategoryResponse(cat))
}

func (h *CategoryHandler) Delete(w http.ResponseWriter, r *http.Request) {
	id := r.PathValue("id")
	if err := h.deleteUC.Execute(r.Context(), id); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusNoContent)
}
