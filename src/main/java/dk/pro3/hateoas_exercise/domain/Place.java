package dk.pro3.hateoas_exercise.domain;

public sealed abstract class Place permits City, Country
{
  private final String name;

  protected Place(String name)
  {
    if (name == null || name.isBlank())
    {
      throw new IllegalArgumentException("A place must have a name");
    }
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public abstract Country getCountry();
}
