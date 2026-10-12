package dk.pro3.hateoas_exercise.repository;

import dk.pro3.hateoas_exercise.domain.City;
import dk.pro3.hateoas_exercise.domain.Country;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository public class InMemoryCityRepository implements CityRepository
{
  // No map of its own: cities live inside their countries (the aggregate), so there
  // is only one copy and nothing to keep in sync. Every answer is built from the
  // countries in CountryRepository (composition, not inheritance).
  private final CountryRepository countryRepository;

  public InMemoryCityRepository(CountryRepository countryRepository)
  {
    this.countryRepository = countryRepository;
  }

  @Override public List<City> findAll()
  {
    return countryRepository.findAll().stream()
        .flatMap(country -> country.getCities().stream()).sorted(
            Comparator.comparing(
                City::getId))   // ids only increase: creation order
        .toList();
  }

  @Override public Optional<City> findById(Long id)
  {
    return findAll().stream().filter(city -> city.getId().equals(id))
        .findFirst();
  }

  @Override public List<City> findByName(String name)
  {
    return findAll().stream()
        .filter(city -> city.getName().equalsIgnoreCase(name)).toList();
  }

  @Override public List<City> findByCountryCode(String countryCode)
  {
    return countryRepository.findByCode(countryCode).map(Country::getCities)
        .orElse(List.of());
  }

  @Override public boolean existsById(Long id)
  {
    return findById(id).isPresent();
  }
}
