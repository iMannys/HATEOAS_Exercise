package dk.pro3.hateoas_exercise.assembler.model;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;

@Relation(collectionRelation = "trips")
public class TripModel extends RepresentationModel<TripModel>
{
  private final String name;
  private final LocalDate startDate;
  private final LocalDate endDate;

  public TripModel(String name, LocalDate startDate, LocalDate endDate)
  {
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getName()
  {
    return name;
  }

  public LocalDate getStartDate()
  {
    return startDate;
  }

  public LocalDate getEndDate()
  {
    return endDate;
  }
}
