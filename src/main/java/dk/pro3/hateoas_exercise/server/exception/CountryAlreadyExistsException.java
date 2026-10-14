package dk.pro3.hateoas_exercise.server.exception;

public class CountryAlreadyExistsException extends RuntimeException
{
  public CountryAlreadyExistsException(String code)
  {
    super("Country " + code + " already exists");
  }
}
