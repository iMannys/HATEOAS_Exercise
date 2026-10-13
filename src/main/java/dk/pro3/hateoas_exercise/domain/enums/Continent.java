package dk.pro3.hateoas_exercise.domain.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Continent
{
  AFRICA("AF", "Africa"), ANTARCTICA("AN", "Antarctica"), ASIA("AS",
    "Asia"), EUROPE("EU", "Europe"), NORTH_AMERICA("NA",
    "North America"), OCEANIA("OC", "Oceania"), SOUTH_AMERICA("SA",
    "South America");

  private final String code;
  private final String displayName;

  Continent(String code, String displayName)
  {
    this.code = code;
    this.displayName = displayName;
  }

  public static Continent fromSearch(String search)
  {
    if (search == null)
      throw new IllegalArgumentException("Search cannot be null");
    String trimmedSearch = search.trim();
    return Arrays.stream(Continent.values()).filter(
        continent -> continent.code.equalsIgnoreCase(trimmedSearch)
            || continent.name().equalsIgnoreCase(trimmedSearch)).findFirst().orElseThrow(() -> new IllegalArgumentException(
            "No continent matches '" + search + "'. Valid codes: "
                + Arrays.stream(Continent.values()).map(Continent::getCode)
                .collect(Collectors.joining(", "))));
  }

  public String getCode()
  {
    return code;
  }

  public String getDisplayName()
  {
    return displayName;
  }
}
