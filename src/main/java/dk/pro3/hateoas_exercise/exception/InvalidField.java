package dk.pro3.hateoas_exercise.exception;

// One failed field in a validation error, e.g. {"field": "population", "message": "..."}
public record InvalidField(String field, String message)
{
}
