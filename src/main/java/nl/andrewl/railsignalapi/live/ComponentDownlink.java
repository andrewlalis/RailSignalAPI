package nl.andrewl.railsignalapi.live;

import lombok.Getter;

public abstract class ComponentDownlink {
	@Getter
	private final long id;

	public ComponentDownlink(long id) {
		this.id = id;
	}

	public abstract void send(Object msg) throws Exception;

	@Override
	public boolean equals(Object o) {
		return o instanceof ComponentDownlink cd && cd.id == this.id;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}
}
