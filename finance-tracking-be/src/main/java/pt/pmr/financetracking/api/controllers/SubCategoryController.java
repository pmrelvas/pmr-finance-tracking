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
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import pt.pmr.financetracking.api.dtos.request.SubCategoryApiRequestDto;
import pt.pmr.financetracking.api.dtos.response.SubCategoryApiResponseDto;
import pt.pmr.financetracking.domain.entities.SubCategoryFilter;
import pt.pmr.financetracking.domain.usecases.CreateSubCategoryUseCase;
import pt.pmr.financetracking.domain.usecases.ReadSubCategoryUseCase;
import pt.pmr.financetracking.domain.usecases.UpdateSubCategoryUseCase;

@Path("/api/v1/categories/{categoryId}/sub-categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class SubCategoryController {

    private final ReadSubCategoryUseCase readSubCategoryUseCase;
    private final CreateSubCategoryUseCase createSubCategoryUseCase;
    private final UpdateSubCategoryUseCase updateSubCategoryUseCase;

    @GET
    public Multi<SubCategoryApiResponseDto> fetchAll(
            @PathParam("categoryId") String categoryId,
            @QueryParam("searchTerm") String searchTerm) {
        SubCategoryFilter filter = SubCategoryFilter.builder()
                .searchTerm(searchTerm)
                .build();
        return readSubCategoryUseCase.executeFindAll(categoryId, filter)
                .onItem().transform(SubCategoryApiResponseDto::new);
    }

    @GET
    @Path("/{id}")
    public Uni<SubCategoryApiResponseDto> fetchById(
            @PathParam("categoryId") String categoryId,
            @PathParam("id") String id) {
        return readSubCategoryUseCase.executeFindById(categoryId, id)
                .map(SubCategoryApiResponseDto::new);
    }

    @POST
    public Uni<Response> create(
            @PathParam("categoryId") String categoryId,
            @Valid SubCategoryApiRequestDto request) {
        return createSubCategoryUseCase.execute(categoryId, request.toEntity())
                .map(SubCategoryApiResponseDto::new)
                .map(dto -> Response.status(Response.Status.CREATED).entity(dto).build());
    }

    @PUT
    @Path("/{id}")
    public Uni<SubCategoryApiResponseDto> update(
            @PathParam("categoryId") String categoryId,
            @PathParam("id") String id,
            @Valid SubCategoryApiRequestDto request) {
        return updateSubCategoryUseCase.execute(categoryId, id, request.toEntity())
                .map(SubCategoryApiResponseDto::new);
    }
}
