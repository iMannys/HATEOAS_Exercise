package dk.pro3.hateoas_exercise.assembler.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "cities")
public class CityModel extends RepresentationModel<CityModel>
{
  private final String name;
  private final int population;
  private final boolean isCapital;

  @JsonCreator
  public CityModel(@JsonProperty("name") String name, @JsonProperty("population") int population, @JsonProperty("capital") boolean isCapital)
  {
    this.name = name;
    this.population = population;
    this.isCapital = isCapital;
  }

  public String getName()
  {
    return name;
  }

  public int getPopulation()
  {
    return population;
  }

  public boolean isCapital()
  {
    return isCapital;
  }
}
