package nl.andrewl.railsignalapi.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Configure resource handlers to use the /app directory for all vue frontend stuff.
		registry.addResourceHandler("/app/**")
				.addResourceLocations("classpath:/app/");

		// Configure resource handlers for driver files.
		registry.addResourceHandler("/driver/**")
				.addResourceLocations("classpath:/driver/");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("*");
	}
}
