package vinix.resources.exceptions;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ValidationError extends StandardError {
  private List<FieldMessage> errors = new ArrayList<>();
  
  public void addError(String field, String message) {
    errors.add(new FieldMessage(field, message));
  }
}
