package nl.andrewl.railsignalapi.rest.dto.link_token;

import nl.andrewl.railsignalapi.model.LinkToken;
import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.rest.dto.component.out.SimpleComponentResponse;

import java.util.Comparator;
import java.util.List;

public record LinkTokenResponse(
		long id,
		String label,
		List<SimpleComponentResponse> components
) {
	public LinkTokenResponse(LinkToken token) {
		this(
				token.getId(),
				token.getLabel(),
				token.getComponents().stream()
						.sorted(Comparator.comparing(Component::getName))
						.map(SimpleComponentResponse::new).toList()
		);
	}
}
