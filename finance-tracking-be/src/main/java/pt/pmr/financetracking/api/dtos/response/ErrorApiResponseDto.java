package pt.pmr.financetracking.api.dtos.response;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
public record ErrorApiResponseDto(
        int errorCode,
        String message,
        List<Field> fields
) {
    @Builder(toBuilder = true)
    public record Field(
            String name,
            String value
    ) {
        public Field(Map.Entry<String, Object> entry) {
            this(
                    entry.getKey(),
                    entry.getValue().toString()
            );
        }
    }
}
