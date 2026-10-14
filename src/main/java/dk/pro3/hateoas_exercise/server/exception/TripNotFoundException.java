package dk.pro3.hateoas_exercise.server.exception;

public class TripNotFoundException extends RuntimeException
{
  public TripNotFoundException(Long id)
  {
    super("Could not find trip " + id);
  }
}
