package dk.pro3.hateoas_exercise.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

// What it should do: translate the exceptions thrown by the services and the domain
// into HTTP responses, in one place for all controllers:
//   …NotFoundException                                  -> 404 Not Found
//   CountryAlreadyExistsException, IllegalStateException -> 409 Conflict (a rule refused it)
//   IllegalArgumentException                             -> 400 Bad Request (invalid input)
//
// Why we need it: without it, Spring turns every uncaught exception into
// 500 Internal Server Error, which tells the client "the server is broken" even when
// they just asked for a country that doesn't exist or sent a negative population.
// Handling it here also keeps try/catch out of the controllers.
@RestControllerAdvice
public class ApiExceptionHandler
{
}
