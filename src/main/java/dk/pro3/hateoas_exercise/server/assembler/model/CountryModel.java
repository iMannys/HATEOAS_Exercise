package dk.pro3.hateoas_exercise.server.assembler.model;

import dk.pro3.hateoas_exercise.domain.enums.Continent;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;

@Relation(collectionRelation = "countries")
public class CountryModel extends RepresentationModel<CountryModel>
{
  private final String code;
  private final Set<Continent> continents;
  private final String name;

  public CountryModel(String name, String code, Set<Continent> continents)
  {
    this.name = name;
    this.code = code;
    this.continents = continents;
  }

  public String getCode()
  {
    return code;
  }

  public Set<Continent> getContinents()
  {
    return continents;
  }

  public String getName()
  {
    return name;
  }
}
