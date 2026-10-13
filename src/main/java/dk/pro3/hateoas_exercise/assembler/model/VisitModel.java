package dk.pro3.hateoas_exercise.assembler.model;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;

@Relation(collectionRelation = "visits")
public class VisitModel extends RepresentationModel<VisitModel>
{
  private final LocalDate arrivalDate;
  private final LocalDate departureDate;
  private final String journal;
  private final String placeName;

  public VisitModel(LocalDate arrivalDate, LocalDate departureDate,
      String journal, String placeName)
  {
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.journal = journal;
    this.placeName = placeName;
  }

  public LocalDate getArrivalDate()
  {
    return arrivalDate;
  }

  public LocalDate getDepartureDate()
  {
    return departureDate;
  }

  public String getJournal()
  {
    return journal;
  }

  public String getPlaceName()
  {
    return placeName;
  }
}
