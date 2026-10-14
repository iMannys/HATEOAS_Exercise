package dk.pro3.hateoas_exercise.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;
import org.springframework.hateoas.mediatype.hal.HalConfiguration.RenderSingleLinks;

// HAL renders a rel with one link as an object and with several as an array, so a
// client would have to handle both shapes. A trip can visit one or many countries,
// so visitedCountries is always rendered as an array.
@Configuration
public class HalConfig
{
  @Bean
  public HalConfiguration halConfiguration()
  {
    return new HalConfiguration().withRenderSingleLinksFor(
        LinkRelation.of("visitedCountries"), RenderSingleLinks.AS_ARRAY);
  }
}
