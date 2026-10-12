package dk.pro3.hateoas_exercise.exception;

public class CountryNotFoundException extends RuntimeException
{
  public CountryNotFoundException(String code)
  {
    super("Could not find country " + code);
  }
}
