package pt.pmr.financetracking.domain.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class CodedException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;
    private final Throwable cause;
    private final Map<String, Object> fields;

    protected CodedException(ErrorCode errorCode, String message, Map<String, Object> fields, Throwable cause) {
        this.errorCode = errorCode;
        this.message = message;
        this.fields = fields == null ? Map.of() : fields;
        this.cause = cause;
    }

    protected CodedException(ErrorCode errorCode, String message, Map<String, Object> fields) {
        this(errorCode, message, fields, null);
    }
}
