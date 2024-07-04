package gg.agenda.api.user;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackages = {"gg.agenda.api"})
@Profile("test-mvc")
public class WebMvcTestApplicationContext {

}
