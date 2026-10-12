package dk.pro3.hateoas_exercise.exception;

public class CountryAlreadyExistsException extends RuntimeException
{
  public CountryAlreadyExistsException(String code)
  {
    super("Country " + code + " already exists");
  }
}
