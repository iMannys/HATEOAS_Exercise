package dk.pro3.hateoas_exercise.service;

import dk.pro3.hateoas_exercise.domain.City;
import dk.pro3.hateoas_exercise.domain.Country;
import dk.pro3.hateoas_exercise.domain.enums.Continent;
import dk.pro3.hateoas_exercise.exception.CityNotFoundException;
import dk.pro3.hateoas_exercise.exception.CountryAlreadyExistsException;
import dk.pro3.hateoas_exercise.exception.CountryNotFoundException;
import dk.pro3.hateoas_exercise.repository.CityRepository;
import dk.pro3.hateoas_exercise.repository.CountryRepository;
import dk.pro3.hateoas_exercise.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CountryService
{
  private final CountryRepository countryRepository;
  private final CityRepository cityRepository;
  private final VisitRepository visitRepository;

  public CountryService(CountryRepository countryRepository, CityRepository cityRepository,
      VisitRepository visitRepository)
  {
    this.countryRepository = countryRepository;
    this.cityRepository = cityRepository;
    this.visitRepository = visitRepository;
  }

  public List<Country> getAllCountries()
  {
    return countryRepository.findAll();
  }

  public Country getCountry(String code)
  {
    return countryRepository.findByCode(code).orElseThrow(() -> new CountryNotFoundException(code));
  }

  public List<Country> getCountriesByContinent(Continent continent)
  {
    return countryRepository.findByContinent(continent);
  }

  public Country createCountry(String name, String code, Set<Continent> continents,
      String capitalName, int capitalPopulation)
  {
    if (countryRepository.existsByCode(code))
    {
      throw new CountryAlreadyExistsException(code);
    }
    Country country = Country.create(name, code, continents, capitalName, capitalPopulation);
    countryRepository.save(country);
    return country;
  }

  public Country changeCapital(String code, Long cityId)
  {
    Country country = getCountry(code);
    City city = cityRepository.findById(cityId).orElseThrow(() -> new CityNotFoundException(cityId));
    country.setCapital(city);
    return country;
  }

  public void deleteCountry(String code)
  {
    getCountry(code); // Throws CountryNotFoundException if missing
    if (!visitRepository.findByCountryCode(code).isEmpty())
    {
      throw new IllegalStateException("Cannot delete a country that has visits.");
    }
    countryRepository.deleteByCode(code);
  }
}
