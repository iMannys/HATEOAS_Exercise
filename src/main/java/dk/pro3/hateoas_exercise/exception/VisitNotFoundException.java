package dk.pro3.hateoas_exercise.exception;

public class VisitNotFoundException extends RuntimeException
{
  public VisitNotFoundException(Long id)
  {
    super("Could not find visit " + id);
  }
}
