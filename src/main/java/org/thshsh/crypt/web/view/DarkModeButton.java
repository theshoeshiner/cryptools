package org.thshsh.crypt.web.view;

import org.thshsh.vaadin.UIUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

@SuppressWarnings("serial")
public class DarkModeButton extends Button {

	public static enum Variant {
		Light,Dark;
		public static Variant getOpposite(Variant v) {
			if(v == Light) return Dark;
			else return Light;
		}
	}
	
	Variant themeVariant = Variant.Light;
		
	public DarkModeButton() {
		this.addClassName("large");
		this.addClassName("icon-only");
		this.setVariant(Variant.Light);
		 this.addClickListener(click -> {
			 setVariant(Variant.getOpposite(themeVariant));
		 });
		 //TODO make this use the session
	}
	
	public void setVariant(Variant v) {
		
		getUI().ifPresent(ui -> {
			ui.getPage().executeJs("document.documentElement.setAttribute(\"theme\",\""+v.name().toLowerCase()+"\")");
		});
		themeVariant = v;
		
		Variant opp = Variant.getOpposite(v);
		UIUtils.setTitle(this, opp.name()+" Mode");
		if(v == Variant.Light) this.setIcon(VaadinIcon.MOON.create());
		else this.setIcon(VaadinIcon.SUN_O.create());
	}


}