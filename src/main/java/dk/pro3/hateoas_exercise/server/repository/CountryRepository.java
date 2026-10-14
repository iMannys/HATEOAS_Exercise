package dk.pro3.hateoas_exercise.server.repository;

import dk.pro3.hateoas_exercise.domain.Country;
import dk.pro3.hateoas_exercise.domain.enums.Continent;

import java.util.List;
import java.util.Optional;

public interface CountryRepository
{
  List<Country> findAll();
  Optional<Country> findByCode(String code);
  Optional<Country> findByName(String name);
  List<Country> findByContinent(Continent continent);
  boolean existsByCode(String code);
  Country save(Country country);
  void deleteByCode(String code);
}
