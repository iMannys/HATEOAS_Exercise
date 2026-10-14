package dk.pro3.hateoas_exercise.server.exception;

public class CityNotFoundException extends RuntimeException
{
  public CityNotFoundException(Long id)
  {
    super("Could not find city " + id);
  }
}
