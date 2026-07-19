package vinix.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponseDTO(

	    String holderName,
	    String document,
	    BigDecimal balance,
	    LocalDateTime createdAt

	) {}
