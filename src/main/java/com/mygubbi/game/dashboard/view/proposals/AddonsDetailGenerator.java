package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.domain.Addon;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.RowReference;
import com.vaadin.ui.HorizontalLayout;

public class AddonsDetailGenerator implements Grid.DetailsGenerator {

    @Override
    public Component getDetails(RowReference rowReference) {
        rowReference.getGrid().scrollTo(rowReference.getItemId());
        Addon module = (Addon) rowReference.getItemId();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setHeight(300, Sizeable.Unit.PIXELS);
        layout.setMargin(true);
        layout.setSpacing(true);
        return layout;
    }

}
