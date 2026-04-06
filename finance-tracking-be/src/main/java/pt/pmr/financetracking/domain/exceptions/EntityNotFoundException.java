package pt.pmr.financetracking.domain.exceptions;

import java.util.Map;

public class EntityNotFoundException extends CodedException {

    private EntityNotFoundException(ErrorCode errorCode, String message, Map<String, Object> fields) {
        super(errorCode, message, fields);
    }

    public static EntityNotFoundException buildForId(Class<?> clazz, String id) {
        return new EntityNotFoundException(
                ErrorCode.ENTITY_NOT_FOUND,
                "%s with id %s not found".formatted(clazz.getSimpleName(), id),
                Map.of("id", id));
    }
}
