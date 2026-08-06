package vinix.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record WithdrawCompletedEvent(
		Long id,
        Long accountId,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal amount,
        LocalDateTime occurredAt
) {}