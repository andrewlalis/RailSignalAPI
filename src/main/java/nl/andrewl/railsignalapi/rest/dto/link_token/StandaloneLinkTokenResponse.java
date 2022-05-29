package nl.andrewl.railsignalapi.rest.dto.link_token;

import nl.andrewl.railsignalapi.model.LinkToken;
import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.rest.dto.component.out.SimpleComponentResponse;

import java.util.Comparator;
import java.util.List;

/**
 * A standalone response for link tokens that includes extra information about
 * the rail system that the token is for.
 * @param id The token id.
 * @param label The token's label.
 * @param components The list of components that the token manages.
 * @param rsId The rail system id.
 * @param rsName The rail system's name.
 */
public record StandaloneLinkTokenResponse (
		long id,
		String label,
		List<SimpleComponentResponse> components,
		long rsId,
		String rsName
) {
	public StandaloneLinkTokenResponse(LinkToken token) {
		this(
				token.getId(),
				token.getLabel(),
				token.getComponents().stream()
						.sorted(Comparator.comparing(Component::getName))
						.map(SimpleComponentResponse::new).toList(),
				token.getRailSystem().getId(),
				token.getRailSystem().getName()
		);
	}
}
