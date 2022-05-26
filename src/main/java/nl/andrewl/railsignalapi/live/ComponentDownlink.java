package nl.andrewl.railsignalapi.live;

import lombok.Getter;

/**
 * A downlink connection to one or more components (linked by a {@link nl.andrewl.railsignalapi.model.LinkToken})
 * which we can send messages to.
 */
public abstract class ComponentDownlink {
	@Getter
	private final long tokenId;

	public ComponentDownlink(long tokenId) {
		this.tokenId = tokenId;
	}

	public abstract void send(Object msg) throws Exception;
	public abstract void shutdown() throws Exception;

	@Override
	public boolean equals(Object o) {
		return o instanceof ComponentDownlink cd && cd.tokenId == this.tokenId;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(tokenId);
	}
}
