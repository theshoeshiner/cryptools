package org.thshsh.crypt.web.view;

import org.vaadin.addons.thshsh.easyrender.EasyRender;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.ValueProvider;

public class AppEasyRender extends EasyRender {

	public static <Source,Target extends Number> LitRenderer<Source> router(
			Class<? extends Component> navigationTarget,
			ValueProvider<Source, ?> idProvider,
			ValueProvider<Source, ?> nameProvider
          ){
		return router(navigationTarget, idProvider, nameProvider, "id", null);
	}
	
}
