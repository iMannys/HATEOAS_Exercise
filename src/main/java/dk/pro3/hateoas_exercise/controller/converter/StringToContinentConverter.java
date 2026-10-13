package dk.pro3.hateoas_exercise.controller.converter;

import dk.pro3.hateoas_exercise.domain.enums.Continent;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToContinentConverter implements Converter<String, Continent>
{
  @Override
  public Continent convert(String search)
  {
    return Continent.fromSearch(search);
  }
}
