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
import pt.pmr.financetracking.api.dtos.request.ExpenseApiRequestDto;
import pt.pmr.financetracking.api.dtos.response.ExpenseApiResponseDto;
import pt.pmr.financetracking.domain.entities.ExpenseFilter;
import pt.pmr.financetracking.domain.usecases.CreateExpenseUseCase;
import pt.pmr.financetracking.domain.usecases.ReadExpenseUseCase;
import pt.pmr.financetracking.domain.usecases.UpdateExpenseUseCase;

import static org.jboss.resteasy.reactive.RestResponse.StatusCode.CREATED;

@Path("/api/v1/expenses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ExpenseController {

    private final ReadExpenseUseCase readExpenseUseCase;
    private final CreateExpenseUseCase createExpenseUseCase;
    private final UpdateExpenseUseCase updateExpenseUseCase;

    @GET
    public Multi<ExpenseApiResponseDto> fetchAll(@QueryParam("searchTerm") String searchTerm) {
        ExpenseFilter filter = ExpenseFilter.builder()
                .searchTerm(searchTerm)
                .build();
        return readExpenseUseCase.executeFindAll(filter)
                .map(ExpenseApiResponseDto::new);
    }

    @GET
    @Path("/{id}")
    public Uni<ExpenseApiResponseDto> fetchById(@PathParam("id") String id) {
        return readExpenseUseCase.executeFindById(id)
                .map(ExpenseApiResponseDto::new);
    }

    @POST
    @ResponseStatus(CREATED)
    public Uni<ExpenseApiResponseDto> create(@Valid ExpenseApiRequestDto payload) {
        return createExpenseUseCase.execute(payload.toEntity())
                .map(ExpenseApiResponseDto::new);
    }

    @PUT
    @Path("/{id}")
    public Uni<ExpenseApiResponseDto> update(@PathParam("id") String id, @Valid ExpenseApiRequestDto payload) {
        return updateExpenseUseCase.execute(id, payload.toEntity())
                .map(ExpenseApiResponseDto::new);
    }
}
