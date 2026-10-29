package team.foodlog.backend.exception.handler;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String message,
        Map<String, List<String>> errors,
        Instant timestamp
) {}
