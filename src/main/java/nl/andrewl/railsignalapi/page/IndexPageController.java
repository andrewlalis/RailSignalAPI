package nl.andrewl.railsignalapi.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = {"/", "/app", "/home", "/index.html", "/index"})
public class IndexPageController {
	@GetMapping
	public String getIndex() {
		return "forward:/app/index.html";
	}
}
