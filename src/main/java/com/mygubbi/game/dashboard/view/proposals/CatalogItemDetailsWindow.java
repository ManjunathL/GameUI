package com.mygubbi.game.dashboard.view.proposals;


import java.util.List;

import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.ProductItem;
import com.mygubbi.game.dashboard.domain.ProductSuggest;
import com.mygubbi.game.dashboard.domain.Proposal;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;

@SuppressWarnings("serial")
public class CatalogItemDetailsWindow extends Window {

    private final AutocompleteField<ProductSuggest> productSuggestField = new AutocompleteField<ProductSuggest>();
    private TextField itemTitleField;
    private Embedded productImage;

    
    private TextField typeField;
    private TextField quantityField;
    private TextField amountField;
    private TextArea descriptionField;

    private Button closeBtn;
    private Button saveBtn;

    private Proposal proposal;

    private ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderUtil());
    

    
    private CatalogItemDetailsWindow(Proposal proposal) {

        this.proposal = proposal;
        
        setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(true);
        setSizeFull();
        setCaption("Add Standard Item");
        
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(new MarginInfo(true, false, false, false));
        
        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        horizontalLayout0.addComponent(buildAddItemBasicFormLeft());
        horizontalLayout0.addComponent(buildAddItemBasicFormRight());
        vLayout.addComponent(horizontalLayout0);
        
        vLayout.addComponent(new Label("</br></br>", ContentMode.HTML));
        vLayout.addComponent(buildFooter());
        
        setContent(vLayout);
        Responsive.makeResponsive(horizontalLayout0);
    }

    
    private FormLayout buildAddItemBasicFormLeft() 
    {
        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.itemTitleField = new TextField("Item Title");	
        formLayoutLeft.addComponent(itemTitleField);
        
        productSuggestField.setCaption("Product");
        productSuggestField.setDelay(200);
        productSuggestField.setStyleName("search");
        productSuggestField.setQueryListener(new AutocompleteQueryListener<ProductSuggest>() 
        {
            @Override
            public void handleUserQuery(AutocompleteField<ProductSuggest> field, String query) 
            {
            	field.clearChoices();
            	handleSearchQuery(field, query);
            }
        });
        productSuggestField.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<ProductSuggest>() 
        {
            @Override
            public void onSuggestionPicked(ProductSuggest page) {
              handleSuggestionSelection(page);
            }
        });
        
        formLayoutLeft.addComponent(productSuggestField);
        this.productImage = new Embedded("");
        formLayoutLeft.addComponent(this.productImage);
        
        return formLayoutLeft;
    }

    private void handleSearchQuery(AutocompleteField<ProductSuggest> field, String query) 
    {
		try 
		{
			List<ProductSuggest> result = proposalDataProvider.getProductSuggestions(query);
			for (ProductSuggest page : result) 
			{
				field.addSuggestion(page, page.getTitle());
			}
			System.out.println("Total results " + result.size());
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
    }
    
    private void handleSuggestionSelection(ProductSuggest suggestion) 
    {
    	System.out.println("Selected " + suggestion.title + ":" + suggestion.amount + ":" + suggestion.id);
    	ProductItem productItem = proposalDataProvider.getProduct(suggestion.id);
    	this.quantityField.setValue("1");
    	this.itemTitleField.setValue(productItem.itemTitle);
    	this.amountField.setValue(productItem.rate + "");
    	this.descriptionField.setValue(productItem.description);
    	this.productImage.setSource(new ExternalResource(productItem.image));
    }
    
    private FormLayout buildAddItemBasicFormRight() 
    {
        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.typeField = new TextField("Type");
        this.typeField.setValue("STANDARD");
        this.typeField.setReadOnly(true);
        formLayoutRight.addComponent(this.typeField);

        this.quantityField = new TextField("Qty");
        this.quantityField.addTextChangeListener(new FieldEvents.TextChangeListener() 
        {
			@Override
			public void textChange(TextChangeEvent event) {
				String qtyStr = event.getText();
				System.out.println(qtyStr);
			}
		});
        
        formLayoutRight.addComponent(this.quantityField);

        this.amountField = new TextField("Amount");
        this.amountField.setEnabled(false);
        formLayoutRight.addComponent(this.amountField);
        
        this.descriptionField = new TextArea("Description");
        this.descriptionField.setEnabled(false);
        formLayoutRight.addComponent(this.descriptionField);
        return formLayoutRight;
    }

    private Component buildFooter() 
    {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        closeBtn = new Button("Close");
        closeBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close_window();
            }
        });
        closeBtn.focus();
        footer.addComponent(closeBtn);
        footer.setSpacing(true);

        saveBtn = new Button("Save");
        saveBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveBtn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) 
            {
                try 
                {
                    // Updated user should also be persisted to database. But
                    // not in this demo.

                    Notification success = new Notification(
                            "Item details saved successfully");
                    success.setDelayMsec(2000);
                    success.setStyleName("bar success small");
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());

                    DashboardEventBus.post(new DashboardEvent.ItemDetailsEvent());
                    close();
                } 
                catch (Exception e) 
                {
                    Notification.show("Error while saving Item details",
                            Type.ERROR_MESSAGE);
                }

            }
        });
        saveBtn.focus();
        saveBtn.setVisible(true);
        footer.addComponent(saveBtn);
        footer.setComponentAlignment(closeBtn, Alignment.TOP_RIGHT);

        return footer;
    }

    public static void open(ProposalHeader proposalHeader) {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Proposal proposal = new Proposal();
        proposal.setProposalHeader(proposalHeader);
        Window w = new CatalogItemDetailsWindow(proposal);
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    public static void close_window() {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
    }
 
}