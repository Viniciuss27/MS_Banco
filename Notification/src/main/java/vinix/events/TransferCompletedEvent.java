package vinix.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record TransferCompletedEvent(
	    Long sourceAccountId,
	    Long targetAccountId,
	    @JsonFormat(shape = JsonFormat.Shape.STRING)
	    BigDecimal amount,
	    LocalDateTime occurredAt
	) {}