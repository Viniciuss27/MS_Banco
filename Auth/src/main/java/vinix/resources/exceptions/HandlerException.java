package vinix.resources.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class HandlerException {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<StandardError> authenticationFailed(
			AuthenticationException e,
			HttpServletRequest request) {

		StandardError err = buildError(
				HttpStatus.UNAUTHORIZED,
				"Falha na autenticação",
				"Email ou senha inválidos",
				request);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
	}
	
	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<StandardError> duplicateEmail(
			DuplicateEmailException e,
			HttpServletRequest request) {
		
		StandardError err = buildError(
				HttpStatus.CONFLICT,
				"Email já cadastrado",
				e.getMessage(),
				request);
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardError> exception(
			Exception e,
			HttpServletRequest request) {

		StandardError err = buildError(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Erro interno no servidor",
				"Erro inesperado, tente novamente mais tarde",
				request);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
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