package nl.andrewl.railsignalapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {
		UserDetailsServiceAutoConfiguration.class
})
public class RailSignalApiApplication {

	public static void main(String[] args) {
		System.setProperty("org.apache.tomcat.websocket.DISABLE_BUILTIN_EXTENSIONS", "true");
		SpringApplication.run(RailSignalApiApplication.class, args);
	}

}
