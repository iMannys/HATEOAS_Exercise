package dk.pro3.hateoas_exercise.domain;

import java.util.concurrent.atomic.AtomicLong;

public final class City extends Place
{
  // AtomicLong: a counter whose operations are atomic (indivisible), so other threads
  // never see it halfway through an update. nextId++ is really read, add, write; two
  // threads can both read 5 and hand out the same id. getAndIncrement() uses
  // compare-and-swap ("set to 6 only if it is still 5", else retry), so every id is
  // unique. Static, so it is shared by all cities. Ids are never reused, not even
  // after a delete.
  private static final AtomicLong NEXT_ID = new AtomicLong(1);

  private final Long id;
  private final Country country;
  private int population;

  public City(String name, Country country, int population)
  {
    super(name);
    if (country == null)
    {
      throw new IllegalArgumentException("A city must belong to a country");
    }
    setPopulation(population);
    this.country = country;
    this.id = NEXT_ID.getAndIncrement();   // after validation, so failed attempts don't use up ids
    country.addCity(this);       // keep Country's list in sync
  }

  public Long getId()
  {
    return id;
  }

  public void setPopulation(int population)
  {
    if (population < 0)
    {
      throw new IllegalArgumentException("Population cannot be negative");
    }
    this.population = population;
  }

  public boolean isCapital()
  {
    return this == country.getCapital();
  }

  @Override public Country getCountry()
  {
    return country;
  }

  public int getPopulation()
  {
    return population;
  }
}
