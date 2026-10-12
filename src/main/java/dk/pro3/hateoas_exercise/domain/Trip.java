package dk.pro3.hateoas_exercise.domain;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Trip
{
  private static final AtomicLong NEXT_ID = new AtomicLong(
      1);   // see City for how AtomicLong works

  private final Long id;
  private String name;
  // CopyOnWriteArrayList: see Country.cities for how it works
  private final List<Visit> visits = new CopyOnWriteArrayList<>();

  public Trip(String name, Visit firstVisit)
  {
    setName(name);
    addVisit(firstVisit);
    this.id = NEXT_ID.getAndIncrement();
  }

  public Long getId()
  {
    return id;
  }

  // synchronized: see Country.addCity. Without it, two removeVisit calls on a trip with
  // two visits could both pass the size check and leave the trip with no visits.
  public synchronized void addVisit(Visit visit)
  {
    if (visit == null)
    {
      throw new IllegalArgumentException("Visit cannot be null");
    }
    if (visits.contains(visit))
    {
      throw new IllegalArgumentException("Visit is already part of this trip");
    }
    visits.add(visit);
  }

  public synchronized void removeVisit(Visit visit)
  {
    if (!visits.contains(visit))
    {
      throw new IllegalArgumentException("Visit is not part of this trip");
    }
    if (visits.size() == 1)
    {
      throw new IllegalStateException("A trip must have at least one visit");
    }
    visits.remove(visit);
  }

  public LocalDate getStartDate()
  {
    return visits.stream().map(Visit::getArrivalDate)
        .min(Comparator.naturalOrder()).orElseThrow();
  }

  public LocalDate getEndDate()
  {
    return visits.stream().map(Visit::getDepartureDate)
        .max(Comparator.naturalOrder()).orElseThrow();
  }

  public Set<Country> getCountries()
  {
    return visits.stream().map(Visit::getCountry)
        .collect(Collectors.toUnmodifiableSet());
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    if (name == null || name.isBlank())
    {
      throw new IllegalArgumentException("A trip must have a name");
    }
    this.name = name;
  }

  public List<Visit> getVisits()
  {
    return List.copyOf(visits);
  }
}
