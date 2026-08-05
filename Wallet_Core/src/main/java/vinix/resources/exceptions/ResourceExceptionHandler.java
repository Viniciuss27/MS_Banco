package vinix.resources.exceptions;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import vinix.services.exceptions.AccountNotFoundException;
import vinix.services.exceptions.InsufficientBalanceException;
import vinix.services.exceptions.InvalidAmountException;
import vinix.services.exceptions.SameAccountTransferException;
import vinix.services.exceptions.TransactionNotFoundException;

@RestControllerAdvice
public class ResourceExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ResourceExceptionHandler.class);

	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<StandardError> accountNotFound(
			AccountNotFoundException e,
			HttpServletRequest request) {
		
		StandardError err = buildError(
				HttpStatus.NOT_FOUND,
				"Conta não encontrada",
				e.getMessage(),
				request);
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
	}

	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<StandardError> insufficientBalance(
			InsufficientBalanceException e,
			HttpServletRequest request) {
		
		StandardError err = buildError(
				HttpStatus.UNPROCESSABLE_ENTITY,
				"Saldo insuficiente",
				e.getMessage(),
				request);
		
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
	}

	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<StandardError> transactionNotFound(
			TransactionNotFoundException e,
			HttpServletRequest request) {
		
		StandardError err = buildError(
				HttpStatus.NOT_FOUND,
				"Transação não encontrada",
				e.getMessage(),
				request);
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
	}

	@ExceptionHandler(DuplicateDocumentException.class)
	public ResponseEntity<StandardError> duplicateDocument(
			DuplicateDocumentException e,
			HttpServletRequest request) {
		
		StandardError err = buildError(
				HttpStatus.CONFLICT,
				"CPF já cadastrado",
				e.getMessage(),
				request);
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
	}
	
	@ExceptionHandler(InvalidAmountException.class)
	public ResponseEntity<StandardError> invalidAmount(
			InvalidAmountException e,
			HttpServletRequest request) {
		
	    StandardError err = buildError(
	    		HttpStatus.BAD_REQUEST,
	    		"Valor inválido",
	    		e.getMessage(),
	    		request);
	    
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
	}

	@ExceptionHandler(SameAccountTransferException.class)
	public ResponseEntity<StandardError> sameAccountTransfer(
			SameAccountTransferException e,
			HttpServletRequest request) {
		
	    StandardError err = buildError(
	    		HttpStatus.BAD_REQUEST,
	    		"Transferência inválida",
	    		e.getMessage(),
	    		request);
	    
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationError> validation(
			MethodArgumentNotValidException e,
			HttpServletRequest request) {
		
		ValidationError err = new ValidationError();
		err.setTimestamp(Instant.now());
		err.setStatus(HttpStatus.BAD_REQUEST.value());
		err.setError("Erro de validação");
		err.setMessage("Um ou mais campos estão inválidos");
		err.setPath(request.getRequestURI());

		for (FieldError f : e.getBindingResult().getFieldErrors()) {
			err.addError(f.getField(), f.getDefaultMessage());
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardError> exception(
			Exception e,
			HttpServletRequest request) {
		
		logger.error(e.getMessage(), e);
		
		StandardError err = buildError(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Erro interno no servidor",
				"Ocorreu um erro inesperado, Tente novamente mais tarde",
				request);
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
	}

	private StandardError buildError(HttpStatus status, String error, String message, HttpServletRequest request) {
		StandardError err = new StandardError();
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError(error);
		err.setMessage(message);
		err.setPath(request.getRequestURI());
		return err;
	}
}