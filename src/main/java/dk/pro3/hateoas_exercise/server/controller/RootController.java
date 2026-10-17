package dk.pro3.hateoas_exercise.server.controller;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// The API's entry point: the only URL a client needs to know. Everything else is
// found by following links from here.
// Every @RestController has @RequestMapping("/api"), so the whole API lives under /api
// and the website (WebsiteController + static files) can have the plain URLs.
// A class-level mapping is used instead of a global path prefix because Spring HATEOAS
// link building (linkTo(methodOn(...))) reads it, so every _links href gets /api too.
// Mapped to both "" and "/" (/api and /api/): Spring treats a trailing slash as a
// different URL.
@RestController
@RequestMapping("/api")
public class RootController
{
  @GetMapping({"", "/"})
  public ResponseEntity<RepresentationModel<?>> getRoot()
  {
    RepresentationModel<?> root = new RepresentationModel<>();
    root.add(linkTo(methodOn(RootController.class).getRoot()).withSelfRel());
    root.add(linkTo(methodOn(CountryController.class).getCountries()).withRel("countries"));
    root.add(linkTo(methodOn(CityController.class).getCities()).withRel("cities"));
    root.add(linkTo(methodOn(VisitController.class).getVisits()).withRel("visits"));
    root.add(linkTo(methodOn(TripController.class).getTrips()).withRel("trips"));
    return ResponseEntity.ok(root);
  }
}
