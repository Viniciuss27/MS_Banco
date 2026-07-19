package vinix.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequestDTO(

	    @NotNull(message = "A conta de origem é obrigatória")
	    Long sourceAccountId,

	    @NotNull(message = "A conta de destino é obrigatória")
	    Long targetAccountId,

	    @NotNull(message = "O valor é obrigatório")
	    @Positive(message = "O valor deve ser maior que zero")
	    BigDecimal amount

	) {

	    @AssertTrue(message = "Conta de origem e destino devem ser diferentes")
	    public boolean isDifferentAccounts() {
	        return !java.util.Objects.equals(sourceAccountId, targetAccountId);
	    }
	}
