package dk.pro3.hateoas_exercise.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Serves the website pages from src/main/resources/static. The API lives under /api
// (see ApiPathConfig), so these URLs are free for the website and need no content
// negotiation. "/" needs no mapping: Spring Boot serves static/index.html there
// automatically (the "welcome page").
// "forward:" hands the request to the static file inside the server, so the browser's
// address bar keeps showing /country instead of /country.html.
// @Controller (not @RestController): the returned String is a view name, not a body.
@Controller
public class WebsiteController
{
  @GetMapping("/country")
  public String countries()
  {
    return "forward:/country.html";
  }

  @GetMapping("/city")
  public String cities()
  {
    return "forward:/city.html";
  }

  @GetMapping("/trip")
  public String trips()
  {
    return "forward:/trip.html";
  }

  @GetMapping("/visit")
  public String visits()
  {
    return "forward:/visit.html";
  }
}
