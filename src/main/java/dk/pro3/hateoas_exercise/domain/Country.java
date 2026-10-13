package dk.pro3.hateoas_exercise.domain;

import dk.pro3.hateoas_exercise.domain.enums.Continent;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Country extends Place
{
  private final String code;              // ISO 3166-1 alpha-2, e.g. "DK"
  private final Set<Continent> continents;
  // CopyOnWriteArrayList: every add/remove copies the whole list into a new array and
  // swaps it in, so readers (e.g. a repository streaming over it on another request
  // thread) always see a complete, unchanging snapshot and never get a
  // ConcurrentModificationException. Writes are slow, reads are fast and lock-free:
  // a good fit for small lists that are read far more often than they change.
  private final List<City> cities = new CopyOnWriteArrayList<>();
  private City capital;                   // 1: always set by create(...)

  private Country(String name, String code, Set<Continent> continents)
  {
    super(name);
    if (code == null || !Set.of(Locale.getISOCountries()).contains(code))
    {
      throw new IllegalArgumentException("Unknown country code: " + code);
    }
    if (continents == null || continents.isEmpty())
    {
      throw new IllegalArgumentException(
          "A country must be part of at least one continent");
    }
    this.code = code;
    this.continents = Set.copyOf(continents);
  }

  // The only way to get a Country. The capital is created after the constructor
  // has finished, so City never sees a half-built Country.
  public static Country create(String name, String code,
      Set<Continent> continents, String capitalName, int capitalPopulation)
  {
    Country country = new Country(name, code, continents);
    country.capital = new City(capitalName, country, capitalPopulation);
    return country;
  }

  // synchronized: only one thread at a time can run a synchronized method on the same
  // Country (they share one lock: the Country object). removeCity and setCapital check
  // something and then act on it; without the lock, setCapital(x) could check that x is
  // in the country, removeCity(x) could then remove it (not the capital yet), and the
  // capital would end up as a removed city. Locking also makes changes visible to
  // other threads, which is why getCapital is synchronized too.
  synchronized void addCity(City city)
  {
    cities.add(city);
  }

  public synchronized void removeCity(City city)
  {
    if (!cities.contains(city))
    {
      throw new IllegalArgumentException("City is not part of this country");
    }
    if (city == capital)
    {
      throw new IllegalStateException(
          "Cannot remove the capital; move the capital to another city first");
    }
    cities.remove(city);
  }

  public synchronized void setCapital(City capital)
  {
    if (!cities.contains(capital))
    {
      throw new IllegalArgumentException(
          "Capital must be a city in this country");
    }
    this.capital = capital;
  }

  @Override public Country getCountry()
  {
    return this;
  }

  public String getCode()
  {
    return code;
  }

  public Set<Continent> getContinents()
  {
    return continents;
  }

  public List<City> getCities()
  {
    return List.copyOf(cities);
  }

  public synchronized City getCapital()
  {
    return capital;
  }
}
