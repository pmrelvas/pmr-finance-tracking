package pt.pmr.financetracking.api;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ext.Provider;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import pt.pmr.financetracking.api.dtos.response.ErrorApiResponseDto;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;

@Provider
@Priority(1)
public class RestExceptionHandler {

    @ServerExceptionMapper(EntityNotFoundException.class)
    public RestResponse<ErrorApiResponseDto> handleNotFoundException(EntityNotFoundException e) {
        return RestResponse.status(
                RestResponse.Status.NOT_FOUND,
                ErrorApiResponseDto.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .message(e.getMessage())
                        .fields(e.getFields() == null
                                ? null
                                : e.getFields().entrySet().stream()
                                .map(ErrorApiResponseDto.Field::new)
                                .toList())
                        .build());
    }
}
