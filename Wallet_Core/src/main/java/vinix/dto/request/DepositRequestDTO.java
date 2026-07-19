package vinix.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DepositRequestDTO(

	    @NotNull(message = "O valor é obrigatório")
	    @Positive(message = "O valor deve ser maior que zero")
	    BigDecimal amount

	) {}
