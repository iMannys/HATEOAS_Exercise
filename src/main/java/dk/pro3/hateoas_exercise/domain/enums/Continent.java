package dk.pro3.hateoas_exercise.domain.enums;

public enum Continent
{
  AFRICA("AF", "Africa"), ANTARCTICA("AN", "Antarctica"), ASIA("AS",
    "Asia"), EUROPE("EU", "Europe"), NORTH_AMERICA("NA",
    "North America"), OCEANIA("OC", "Oceania"), SOUTH_AMERICA("SA",
    "South America");

  private final String code;
  private final String name;

  Continent(String code, String name)
  {
    this.code = code;
    this.name = name;
  }

  public String getCode()
  {
    return code;
  }

  public String getName()
  {
    return name;
  }
}
