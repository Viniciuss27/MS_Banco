package vinix.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import vinix.entities.TransactionType;

public record TransactionResponseDTO(

	    AccountResponseSummaryDTO account,
	    TransactionType type,

	    @JsonFormat(shape = JsonFormat.Shape.STRING)
	    BigDecimal amount,

	    @JsonFormat(shape = JsonFormat.Shape.STRING)
	    BigDecimal balanceAfter,
	    String description,
	    LocalDateTime createdAt

	) {}