package pt.pmr.financetracking.api.controllers;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.ResponseStatus;
import pt.pmr.financetracking.api.dtos.request.CategoryApiRequestDto;
import pt.pmr.financetracking.api.dtos.response.CategoryApiResponseDto;
import pt.pmr.financetracking.domain.entities.CategoryFilter;
import pt.pmr.financetracking.domain.usecases.CreateCategoryUseCase;
import pt.pmr.financetracking.domain.usecases.ReadCategoryUseCase;
import pt.pmr.financetracking.domain.usecases.UpdateCategoryUseCase;

import static org.jboss.resteasy.reactive.RestResponse.StatusCode.CREATED;

@Path("/api/v1/categories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class CategoryController {

    private final ReadCategoryUseCase readCategoryUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;

    @GET
    public Multi<CategoryApiResponseDto> fetchAll(@QueryParam("searchTerm") String searchTerm) {
        CategoryFilter filter = CategoryFilter.builder()
                .searchTerm(searchTerm)
                .build();
        return readCategoryUseCase.executeFindAll(filter)
                .map(CategoryApiResponseDto::new);
    }

    @GET
    @Path("/{id}")
    public Uni<CategoryApiResponseDto> fetchById(@PathParam("id") String id) {
        return readCategoryUseCase.executeFindById(id)
                .map(CategoryApiResponseDto::new);
    }

    @POST
    @ResponseStatus(CREATED)
    public Uni<CategoryApiResponseDto> create(@Valid CategoryApiRequestDto payload) {
        return createCategoryUseCase.execute(payload.toEntity())
                .map(CategoryApiResponseDto::new);
    }

    @PUT
    @Path("/{id}")
    public Uni<CategoryApiResponseDto> update(@PathParam("id") String id, @Valid CategoryApiRequestDto payload) {
        return updateCategoryUseCase.execute(id, payload.toEntity())
                .map(CategoryApiResponseDto::new);
    }
}
