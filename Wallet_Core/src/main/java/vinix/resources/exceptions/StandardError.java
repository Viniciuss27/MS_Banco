package vinix.resources.exceptions;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class StandardError {

	private Instant timestamp;
	private Integer status;
	private String path;
	private String message;
	private String error;
	
}
