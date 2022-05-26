package nl.andrewl.railsignalapi.live.dto;

/**
 * A message that's sent regarding an error that occurred.
 */
public class ErrorMessage extends ComponentMessage {
	public String message;

	public ErrorMessage(long cId, String message) {
		super(cId, "ERROR");
		this.message = message;
	}
}
