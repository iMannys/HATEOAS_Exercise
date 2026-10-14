package dk.pro3.hateoas_exercise.domain;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

public final class Visit
{
  private static final AtomicLong NEXT_ID = new AtomicLong(
      1);   // see City for how AtomicLong works

  private final Long id;
  private final Place place;          // 1: a visit takes place at exactly one place
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private String journal;

  public Visit(Place place, LocalDate arrivalDate, LocalDate departureDate,
      String journal)
  {
    if (place == null)
    {
      throw new IllegalArgumentException("A visit must take place somewhere");
    }
    this.place = place;
    changeDates(arrivalDate, departureDate);
    this.journal = journal;
    this.id = NEXT_ID.getAndIncrement();
  }

  public Long getId()
  {
    return id;
  }

  // synchronized: changeDates sets two fields. Without the lock, another thread could
  // read in between and see the new arrival with the old departure (possibly departure
  // before arrival). The getters share the same lock, so they always see a matching pair.
  public synchronized void changeDates(LocalDate arrivalDate, LocalDate departureDate)
  {
    if (arrivalDate == null || departureDate == null)
    {
      throw new IllegalArgumentException("Dates are required");
    }
    if (departureDate.isBefore(arrivalDate))
    {
      throw new IllegalArgumentException("Departure cannot be before arrival");
    }
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
  }

  public Country getCountry()
  {
    return place.getCountry();
  }

  public Place getPlace()
  {
    return place;
  }

  public synchronized LocalDate getArrivalDate()
  {
    return arrivalDate;
  }

  public synchronized LocalDate getDepartureDate()
  {
    return departureDate;
  }

  public String getJournal()
  {
    return journal;
  }

  public void setJournal(String journal)
  {
    this.journal = journal;
  }
}
