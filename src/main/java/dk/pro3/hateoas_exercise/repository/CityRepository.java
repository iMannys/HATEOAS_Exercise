package dk.pro3.hateoas_exercise.repository;

import dk.pro3.hateoas_exercise.domain.City;

import java.util.List;
import java.util.Optional;

// Read-only: cities are part of the Country aggregate. Create with new City(...),
// remove with country.removeCity(...), and they are saved with their country.
public interface CityRepository
{
  List<City> findAll();
  Optional<City> findById(Long id);
  List<City> findByName(String name);
  List<City> findByCountryCode(String countryCode);
  boolean existsById(Long id);
}
