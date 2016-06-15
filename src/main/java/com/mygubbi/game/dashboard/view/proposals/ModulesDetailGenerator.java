package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.domain.JsonPojo.ModuleNew;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.RowReference;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class ModulesDetailGenerator implements Grid.DetailsGenerator {

    @Override
    public Component getDetails(RowReference rowReference) {
        rowReference.getGrid().scrollTo(rowReference.getItemId());
        ModuleNew module = (ModuleNew) rowReference.getItemId();

        HorizontalLayout layout = new HorizontalLayout();
        layout.setHeight(300, Sizeable.Unit.PIXELS);
        layout.setMargin(true);
        layout.setSpacing(true);
        ExternalResource resource =
                new ExternalResource(module.getImage());
        Embedded image = new Embedded("", resource);
        image.setHeight(32, Sizeable.Unit.PIXELS);
        image.setWidth(32, Sizeable.Unit.PIXELS);
        layout.addComponent(image);
        Label nameLabel = new Label("<h1>" + module.getImportedModule() + "</h1>", ContentMode.HTML);
        layout.addComponent(nameLabel);
        layout.setExpandRatio(nameLabel, 1.0f);
        return layout;
    }

}
