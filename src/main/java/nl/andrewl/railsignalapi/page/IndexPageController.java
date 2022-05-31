package nl.andrewl.railsignalapi.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Helper controller that redirects some common starting points to our embedded
 * web app's index page.
 */
@Controller
@RequestMapping(path = {"/", "/app", "/app/**", "/home", "/index.html", "/index"})
public class IndexPageController {
	@GetMapping
	public String getIndex() {
		return "forward:/app/index.html";
	}
}
