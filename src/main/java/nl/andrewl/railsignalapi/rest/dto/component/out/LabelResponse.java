package nl.andrewl.railsignalapi.rest.dto.component.out;

import nl.andrewl.railsignalapi.model.component.Label;

public class LabelResponse extends ComponentResponse {
	public String text;
	public LabelResponse(Label lbl) {
		super(lbl);
		this.text = lbl.getText();
	}
}
