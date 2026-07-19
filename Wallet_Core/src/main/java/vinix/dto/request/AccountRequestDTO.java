package vinix.dto.request;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AccountRequestDTO(

	    @NotBlank(message = "O nome do titular é obrigatório")
	    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
	    String holderName,

	    @NotBlank(message = "O documento é obrigatório")
	    @CPF(message = "CPF inválido")
	    String document,

	    @NotNull(message = "Saldo inicial é obrigatório")
	    @PositiveOrZero(message = "Saldo inicial não pode ser negativo")
	    @Digits(integer = 10, fraction = 2)
	    BigDecimal initialBalance

	) {}
