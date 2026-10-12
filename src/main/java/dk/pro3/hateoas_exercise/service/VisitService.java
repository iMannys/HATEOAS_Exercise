package dk.pro3.hateoas_exercise.service;

import dk.pro3.hateoas_exercise.domain.Visit;
import dk.pro3.hateoas_exercise.exception.CountryNotFoundException;
import dk.pro3.hateoas_exercise.exception.VisitNotFoundException;
import dk.pro3.hateoas_exercise.repository.CountryRepository;
import dk.pro3.hateoas_exercise.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Lookups and edits of existing visits. Visits are created and removed through
// TripService, because Trip is their aggregate root.
@Service
public class VisitService
{
  private final VisitRepository visitRepository;
  private final CountryRepository countryRepository;

  public VisitService(VisitRepository visitRepository, CountryRepository countryRepository)
  {
    this.visitRepository = visitRepository;
    this.countryRepository = countryRepository;
  }

  public List<Visit> getAllVisits()
  {
    return visitRepository.findAll();
  }

  public Visit getVisit(Long id)
  {
    return visitRepository.findById(id).orElseThrow(() -> new VisitNotFoundException(id));
  }

  // Includes visits to the country's cities
  public List<Visit> getVisitsByCountry(String countryCode)
  {
    if (!countryRepository.existsByCode(countryCode))
    {
      throw new CountryNotFoundException(countryCode);
    }
    return visitRepository.findByCountryCode(countryCode);
  }

  public Visit changeDates(Long id, LocalDate arrivalDate, LocalDate departureDate)
  {
    Visit visit = getVisit(id);
    visit.changeDates(arrivalDate, departureDate);
    return visit;
  }

  public Visit updateJournal(Long id, String journal)
  {
    Visit visit = getVisit(id);
    visit.setJournal(journal);
    return visit;
  }
}
