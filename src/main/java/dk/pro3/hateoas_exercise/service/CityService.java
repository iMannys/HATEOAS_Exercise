package dk.pro3.hateoas_exercise.service;

import dk.pro3.hateoas_exercise.domain.City;
import dk.pro3.hateoas_exercise.domain.Country;
import dk.pro3.hateoas_exercise.exception.CityNotFoundException;
import dk.pro3.hateoas_exercise.exception.CountryNotFoundException;
import dk.pro3.hateoas_exercise.repository.CityRepository;
import dk.pro3.hateoas_exercise.repository.CountryRepository;
import dk.pro3.hateoas_exercise.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService
{
  private final CityRepository cityRepository;
  private final CountryRepository countryRepository;
  private final VisitRepository visitRepository;

  public CityService(CityRepository cityRepository, CountryRepository countryRepository,
      VisitRepository visitRepository)
  {
    this.cityRepository = cityRepository;
    this.countryRepository = countryRepository;
    this.visitRepository = visitRepository;
  }

  public List<City> getAllCities()
  {
    return cityRepository.findAll();
  }

  public City getCity(Long id)
  {
    return cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException(id));
  }

  public List<City> getCitiesByName(String name)
  {
    return cityRepository.findByName(name);
  }

  public List<City> getCitiesByCountry(String countryCode)
  {
    // Without this check an unknown code would just give an empty list
    if (!countryRepository.existsByCode(countryCode))
    {
      throw new CountryNotFoundException(countryCode);
    }
    return cityRepository.findByCountryCode(countryCode);
  }

  public City createCity(String countryCode, String name, int population)
  {
    Country country = countryRepository.findByCode(countryCode)
        .orElseThrow(() -> new CountryNotFoundException(countryCode));
    // Registers itself with the country, so it is saved with it (same aggregate)
    return new City(name, country, population);
  }

  public City updatePopulation(Long id, int population)
  {
    City city = getCity(id);
    city.setPopulation(population);
    return city;
  }

  public void deleteCity(Long id)
  {
    City city = getCity(id);
    if (!visitRepository.findByPlace(city).isEmpty())
    {
      throw new IllegalStateException("Cannot delete a city that has visits");
    }
    city.getCountry().removeCity(city);   // throws IllegalStateException if it is the capital
  }
}
