package dk.pro3.hateoas_exercise.repository;

import dk.pro3.hateoas_exercise.domain.Country;
import dk.pro3.hateoas_exercise.domain.enums.Continent;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository public class InMemoryCountryRepository implements CountryRepository
{
  private final Map<String, Country> countries = new ConcurrentHashMap<>();

  @Override public List<Country> findAll()
  {
    return List.copyOf(countries.values());
  }

  @Override public Optional<Country> findByCode(String code)
  {
    return Optional.ofNullable(countries.get(code));
  }

  @Override public Optional<Country> findByName(String name)
  {
    return countries.values().stream()
        .filter(country -> country.getName().equals(name)).findFirst();
  }

  @Override public List<Country> findByContinent(Continent continent)
  {
    return countries.values().stream()
        .filter(country -> country.getContinents().contains(continent))
        .toList();
  }

  @Override public boolean existsByCode(String code)
  {
    return countries.containsKey(code);
  }

  @Override public Country save(Country country)
  {
    countries.put(country.getCode(), country);
    return country;
  }

  @Override public void deleteByCode(String code)
  {
    countries.remove(code);
  }
}
