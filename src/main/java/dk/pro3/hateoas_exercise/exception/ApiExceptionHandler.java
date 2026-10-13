package dk.pro3.hateoas_exercise.exception;

import dk.pro3.hateoas_exercise.domain.enums.Continent;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

// Every error is answered with a ProblemDetail (RFC 9457, application/problem+json),
// so clients only have to understand one error format. Extending
// ResponseEntityExceptionHandler makes Spring's own errors (malformed JSON, wrong
// HTTP method, missing parameter, ...) use the same format.
@RestControllerAdvice public class ApiExceptionHandler
    extends ResponseEntityExceptionHandler
{
  @ExceptionHandler({CityNotFoundException.class,
      CountryNotFoundException.class, TripNotFoundException.class,
      VisitNotFoundException.class})
  public ProblemDetail handleNotFoundException(RuntimeException e)
  {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler
  public ProblemDetail handleCountryAlreadyExistsException(
      CountryAlreadyExistsException e)
  {
    return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
  }

  @ExceptionHandler
  public ProblemDetail handleIllegalArgumentException(IllegalArgumentException e)
  {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler
  public ProblemDetail handleIllegalStateException(IllegalStateException e)
  {
    return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
  }

  // Thrown by @Valid. The parent class already handles it, so it is overridden
  // here (a separate @ExceptionHandler for it would clash at startup). Adds which
  // fields failed, so a client can point the user at the right input.
  @Override protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e, HttpHeaders headers,
      HttpStatusCode status, WebRequest request)
  {
    List<InvalidField> invalidFields = e.getBindingResult().getFieldErrors()
        .stream()
        .map(error -> new InvalidField(error.getField(), error.getDefaultMessage()))
        .toList();

    ProblemDetail problemDetail = e.getBody();
    problemDetail.setDetail("One or more fields are invalid");
    problemDetail.setProperty("errors", invalidFields);
    return handleExceptionInternal(e, problemDetail, headers, status, request);
  }

  @Override
  protected @Nullable ResponseEntity<Object> handleTypeMismatch(
      TypeMismatchException e, HttpHeaders headers, HttpStatusCode status, WebRequest request)
  {
    if (e.getRequiredType() == Continent.class)
    {
      String message = e.getMostSpecificCause().getMessage();

      ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
      return handleExceptionInternal(e, problemDetail, headers, status, request);
    }
    return super.handleTypeMismatch(e, headers, status, request);
  }
}
