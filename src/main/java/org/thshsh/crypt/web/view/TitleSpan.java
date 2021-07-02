package org.thshsh.crypt.web.view;

import com.vaadin.flow.component.html.Span;

@SuppressWarnings("serial")
public class TitleSpan extends Span {

	public TitleSpan() {
        Span odm = new Span("¢");
        Span loader = new Span("ryptools");
        add(odm,loader);
        addClassName("logo-text");
	}

}
