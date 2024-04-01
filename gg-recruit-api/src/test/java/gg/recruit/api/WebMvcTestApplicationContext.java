package gg.recruit.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackages = {"gg.recruit.api"})
@Profile("test-mvc")
public class WebMvcTestApplicationContext {

}
