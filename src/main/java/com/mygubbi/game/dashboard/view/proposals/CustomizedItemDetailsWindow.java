package com.mygubbi.game.dashboard.view.proposals;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.Addon;
import com.mygubbi.game.dashboard.domain.Module;
import com.mygubbi.game.dashboard.domain.Module.ImportStatus;
import com.mygubbi.game.dashboard.domain.Proposal;
import com.mygubbi.game.dashboard.domain.JsonPojo.SimpleComboItem;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.datenhahn.vaadin.componentrenderer.grid.ComponentGrid;
import de.datenhahn.vaadin.componentrenderer.grid.ComponentGridDecorator;

@SuppressWarnings("serial")
public class CustomizedItemDetailsWindow extends Window {

    private TextField itemTitleField;
    private ComboBox roomSelection;
    private ComboBox productSelection;
    private ComboBox carcassMaterialSelection;
    
    private TextField typeField;
    private ComboBox shutterFinishSelection;
    
    private Upload uploadCtrl;
    private File uploadFile;
    private String uploadPath = "/tmp/";
    
    private TabSheet tabSheet;
    private Button closeBtn;
    private Button saveBtn;
    
    private Proposal proposal;

    private ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderUtil());
    
    private CustomizedItemDetailsWindow(Proposal proposal) {

        this.proposal = proposal;
        
        setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
        setSizeFull();
        setResizable(false);
        setClosable(true);
        setCaption("Add Customized Item");
        
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setSizeFull();
        vLayout.setMargin(new MarginInfo(true, true, true, true));
        setContent(vLayout);
        Responsive.makeResponsive(this);
        
        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        horizontalLayout0.addComponent(buildAddItemBasicFormLeft());
        horizontalLayout0.addComponent(buildAddItemBasicFormRight());
        vLayout.addComponent(horizontalLayout0);
        vLayout.setExpandRatio(horizontalLayout0, 0.4f);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();
        
        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addTab(buildModulesForm(), "Modules");
        tabSheet.addTab(buildAddonsForm(), "Addons");
        tabSheet.addTab(buildAttachmentsForm(), "Attachments");
        tabSheet.setEnabled(true);
        horizontalLayout1.addComponent(tabSheet);
        vLayout.addComponent(horizontalLayout1);
        vLayout.setExpandRatio(horizontalLayout1, 0.5f);

        Component footerLayOut = buildFooter();
        vLayout.addComponent(footerLayOut);
        vLayout.setExpandRatio(footerLayOut, 0.1f);
    }

    /*
     * Add Item basic section
     */
    private FormLayout buildAddItemBasicFormLeft() 
    {
        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.itemTitleField = new TextField("Item Title");	
        formLayoutLeft.addComponent(itemTitleField);

        this.productSelection = getSimpleItemFilledCombo("Product", "product_data");
        formLayoutLeft.addComponent(this.productSelection);

    	this.carcassMaterialSelection = getSimpleItemFilledCombo("Carcass Material", "carcass_material_data");
        formLayoutLeft.addComponent(this.carcassMaterialSelection);

        this.shutterFinishSelection = getSimpleItemFilledCombo("Shutter Finish", "shutter_material_data");
        formLayoutLeft.addComponent(this.shutterFinishSelection);

        return formLayoutLeft;
    }

    private FormLayout buildAddItemBasicFormRight() 
    {
        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.typeField = new TextField("Type");
        this.typeField.setValue("CUSTOMIZED");
        this.typeField.setReadOnly(true);
        formLayoutRight.addComponent(typeField);

        this.roomSelection = getSimpleItemFilledCombo("Room", "room_data");
        formLayoutRight.addComponent(this.roomSelection);

        formLayoutRight.addComponent(getUploadControl());
        return formLayoutRight;
    }

    private Component getUploadControl()
    {
    	this.uploadCtrl = new Upload("Import Quotation Sheet", new Upload.Receiver() 
    	{
			@Override
			public OutputStream receiveUpload(String filename, String mimeType) 
			{
				System.out.println("Received uploap - " + filename);
				FileOutputStream fos = null;
			    uploadFile = new File(uploadPath + filename);
			    try 
			    {
			         fos = new FileOutputStream(uploadFile);
			    } 
			    catch (final java.io.FileNotFoundException e) 
			    {
			         e.printStackTrace();
			         return null;
			    }
				return fos;
			}
		});

    	this.uploadCtrl.addProgressListener(new Upload.ProgressListener() {
			
			@Override
			public void updateProgress(long readBytes, long contentLength) 
			{
				System.out.println("Progress " + (readBytes * 100 / contentLength));
			}
		});

    	this.uploadCtrl.addSucceededListener(new Upload.SucceededListener() 
    	{
			@Override
			public void uploadSucceeded(SucceededEvent event) 
			{
				System.out.println("Successfully uploaded - " + event.getFilename());
			}
		});

    	this.uploadCtrl.addFailedListener(new Upload.FailedListener() 
    	{
			@Override
			public void uploadFailed(FailedEvent event) {
				System.out.println("Error upload - "+ event.getFilename() + " :" + event.getReason().getMessage());
			}
		});
    	return uploadCtrl;
    }
    
    /*
     * Modules Section
     */
    List<String> carcassMaterialL = new ArrayList<>();
    List<String> shutterFinishL = new ArrayList<>();
    List<String> finishL = new ArrayList<>();
    List<String> colorL = new ArrayList<>();
    
    private Component buildModulesForm() 
    {
        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setSizeFull();
        
        List<SimpleComboItem> carcassMaterials = proposalDataProvider.getComboItems("carcass_material_data");
        carcassMaterials.forEach(item -> carcassMaterialL.add(item.title));
        
        List<SimpleComboItem> shutterMaterials = proposalDataProvider.getComboItems("shutter_material_data");
        shutterMaterials.forEach(item -> shutterFinishL.add(item.title));
        
        List<SimpleComboItem> colors = proposalDataProvider.getComboItems("color_data");
        colors.forEach(item -> colorL.add(item.title));
        
        ComponentGrid<Module> componentGrid = new ComponentGrid<>(Module.class);
        componentGrid.setSizeFull();
        
        componentGrid.setRows(getModules(""));
        componentGrid.setDetailsGenerator(new ModulesDetailGenerator());
        
        componentGrid.addComponentColumn("importStatus", module -> createModuleStatus(componentGrid.getComponentGridDecorator(), module));
        
        componentGrid.addComponentColumn("mgModuleCode", module -> 
        							createMgModuleSelector(componentGrid.getComponentGridDecorator(), module));
        
        componentGrid.addComponentColumn("image", module -> createModuleImage(componentGrid.getComponentGridDecorator(), module));
        
        componentGrid.addComponentColumn("carcassMaterial", module -> createModuleCM(componentGrid.getComponentGridDecorator(), module));
        componentGrid.addComponentColumn("shutterMaterial", module -> createModuleSM(componentGrid.getComponentGridDecorator(), module));
        componentGrid.addComponentColumn("color", module -> createModuleColor(componentGrid.getComponentGridDecorator(), module));
        
        componentGrid.setColumnOrder("importStatus", "seqNo", "importedModuleCode", "image", "mgModuleCode", "carcassMaterial", "shutterMaterial", "color", "qty", "amount");
        componentGrid.setColumns("importStatus", "seqNo", "importedModuleCode", "image", "mgModuleCode", "carcassMaterial", "shutterMaterial", "color", "qty", "amount");
        
        HeaderRow mainHeader = componentGrid.getDefaultHeaderRow();
        mainHeader.getCell("importStatus").setText("");
        mainHeader.getCell("seqNo").setText("#");
        mainHeader.getCell("importedModuleCode").setText("Imported Module");
        mainHeader.getCell("mgModuleCode").setText("Mg Module");
        mainHeader.getCell("carcassMaterial").setText("Carcass Material");
        mainHeader.getCell("shutterMaterial").setText("Shutter Finish");
        mainHeader.getCell("color").setText("Color");
        mainHeader.getCell("qty").setText("Qty");
        mainHeader.getCell("amount").setText("Amount");

        hLayout.addComponent(componentGrid);
        hLayout.setExpandRatio(componentGrid, 1);
        
        return componentGrid;
    }

    public enum Status { DEFAULT_MATCHED, MATCHED , NOT_MATCHED } 
	private Component createModuleStatus(ComponentGridDecorator<Module> componentGridDecorator, Module module)
    {
		Image image = new Image("");
        
		switch (module.getImportStatus()) {
		case DEFAULT_MATCHED:
			image.setSource(new ThemeResource("img/default-matched.png"));
			break;

		case MATCHED:
			image.setSource(new ThemeResource("img/active.png"));
			break;

		case NOT_MATCHED:
			image.setSource(new ThemeResource("img/not_matched.png"));
			break;
		}
		image.setHeight(15, Sizeable.Unit.PIXELS);
        image.setWidth(15, Sizeable.Unit.PIXELS);
        return image;
    }

    private Component createModuleCM(ComponentGridDecorator<Module> componentGridDecorator, Module module) 
    {
        ComboBox select = new ComboBox();
        select.setWidth("150px");
        select.setHeight("30px");
        select.addItems(carcassMaterialL);
        select.setPropertyDataSource(new BeanItem<>(module).getItemProperty("carcassMaterial"));
        select.addValueChangeListener(e -> 
        {
        	componentGridDecorator.refresh();
        });
        return select;
	}

    private Component createModuleSM(ComponentGridDecorator<Module> componentGridDecorator, Module module) 
    {
        ComboBox select = new ComboBox();
        select.setWidth("150px");
        select.setHeight("30px");
        select.addItems(shutterFinishL);
        select.setPropertyDataSource(new BeanItem<>(module).getItemProperty("shutterMaterial"));
        select.addValueChangeListener(e -> 
        {
        	componentGridDecorator.refresh();
        });
        return select;
	}

    private Component createModuleColor(ComponentGridDecorator<Module> componentGridDecorator, Module module) 
    {
        ComboBox select = new ComboBox();
        select.setWidth("150px");
        select.setHeight("30px");
        select.addItems(colorL);
        select.setPropertyDataSource(new BeanItem<>(module).getItemProperty("color"));
        select.addValueChangeListener(e -> 
        {
        	componentGridDecorator.refresh();
        });
        return select;
	}

	public Component createModuleImage(ComponentGridDecorator<Module> componentGridDecorator, Module module)
    {
        ExternalResource resource =
        		new ExternalResource(module.getImagePath());
        Embedded image = new Embedded("", resource);
        image.setHeight(32, Sizeable.Unit.PIXELS);
        image.setWidth(32, Sizeable.Unit.PIXELS);
        return image;
    }
    
    
    public Component createMgModuleSelector(ComponentGridDecorator<Module> componentGridDecorator, Module
            module) 
    {
        ComboBox select = new ComboBox();
        select.setWidth("150px");
        select.setHeight("30px");
        
        if ( module.getMgModuleImageMap() != null )
        	select.addItems(module.getMgModuleImageMap().keySet());
        
        select.setPropertyDataSource(new BeanItem<>(module).getItemProperty("mgModuleCode"));
        select.addValueChangeListener(e -> 
        {
            int index = componentGridDecorator.getGrid().getContainerDataSource().indexOfId(module);
            System.out.println("" + e.getProperty().getValue().toString() + ":" + index);
        	
            componentGridDecorator.getGrid().getContainerDataSource().removeItem(module);
        	module.setImagePath(module.getMgModuleImageMap().get(e.getProperty().getValue()));
        	componentGridDecorator.getGrid().getContainerDataSource().addItemAt(index, module);
        	componentGridDecorator.refresh();
        });
        return select;
    }
    
    private List<Module> getModules(String string) 
    {
        Map<String, String> map = new HashMap<>();
        map.put("K2DU4572", "http://ecx.images-amazon.com/images/I/5182QOOr2oL._SS40_.jpg");
        map.put("K2DU4571", "http://ecx.images-amazon.com/images/I/41n4ib0WvdL._SS36_.jpg");
        map.put("SINK1072", "http://i.ebayimg.com/images/g/N9QAAOxyBjBTWAe1/s-l64.jpg");
    	
    	List<Module> moduleL = new ArrayList<>(); 
    	Module module = new Module();
    	module.setSeqNo(1);
    	module.setImportedModuleCode("ADD1_536");
        module.setMgModuleCode("K2DU4572");
        module.setDescription("Kitche 2 drawer unit");
        module.setW(440.100);
        module.setD(100.00);
        module.setH(200.00);
        module.setImagePath(map.get("K2DU4572"));
        module.setCarcassMaterial("PLY");
        module.setCarcassMaterialId(1);
        module.setShutterMaterial("PLY/LAM");
        module.setShutterMaterialId(5);
        module.setFinishId(1);
        module.setColor("20191 HGL FROSTY WHITE");
        module.setQty(1);
        module.setAmount(1300.00);
        module.setImportStatus(ImportStatus.MATCHED);
        module.setMgModuleImageMap(map);
        moduleL.add(module);
        
        Module module2 = new Module();
        module2.setSeqNo(2);
        module2.setImportedModuleCode("FUL1_536");
        module2.setMgModuleCode("K2DU4571");
        module2.setDescription("Kitche 2 drawer unit");
        module2.setW(440.100);
        module2.setD(100.00);
        module2.setH(200.00);
        module2.setImagePath(map.get("K2DU4571"));
        module2.setCarcassMaterial("PLY");
        module2.setCarcassMaterialId(2);
        module2.setShutterMaterial("PLY/LAM");
        module2.setShutterMaterialId(6);
        module2.setFinishId(1);
        module2.setColor("20191 HGL FROSTY WHITE");
        module2.setQty(2);
        module2.setAmount(2600.00);
        module2.setImportStatus(ImportStatus.DEFAULT_MATCHED);
        module2.setMgModuleImageMap(map);
        moduleL.add(module2);
        
        Module module3 = new Module();
        module3.setSeqNo(3);
        module3.setImportedModuleCode("FUL1_537");
        module3.setMgModuleCode("");
        module3.setDescription("Kitche 2 drawer unit");
        module3.setW(440.100);
        module3.setD(100.00);
        module3.setH(200.00);
        module3.setImagePath("");
        module3.setCarcassMaterial("PLY");
        module3.setCarcassMaterialId(2);
        module3.setShutterMaterial("PLY/LAM");
        module3.setShutterMaterialId(6);
        module3.setFinishId(1);
        module3.setColor("20191 HGL FROSTY WHITE");
        module3.setQty(2);
        module3.setAmount(2600.00);
        module3.setImportStatus(ImportStatus.NOT_MATCHED);
        moduleL.add(module3);
        
		return moduleL;
	}

    /*
     * Addons section
     */
    
	private Component buildAddonsForm() 
    {
        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();

        List<String> addonTypes = proposalDataProvider.getAddonTypes();
        List<String> addonNameL = new ArrayList<>();
        
        Map<String, String> addonTypeAndNameMap = new HashMap<>();
        
        addonNameL.add("4T HANDLES");
        addonNameL.add("GRANITE 4MX");
        addonNameL.add("DADO");

        ComponentGrid<Addon> componentGrid = new ComponentGrid<>(Addon.class);
        componentGrid.setSizeFull();
        
        componentGrid.setRows(getAddon(""));
        componentGrid.setDetailsGenerator(new AddonsDetailGenerator());
        
        componentGrid.addComponentColumn("type", addon -> 
        							createAddonTypeSelector(componentGrid.getComponentGridDecorator(), addon, addonTypes));
        
        componentGrid.addComponentColumn("name", addon -> createAddonNameSelector(componentGrid.getComponentGridDecorator(), addon, addonNameL));
        componentGrid.addComponentColumn("imagePath", addon -> createAddonImage(componentGrid.getComponentGridDecorator(), addon));
        componentGrid.addComponentColumn("qtyArea", addon -> createAddonQty(componentGrid.getComponentGridDecorator(), addon));
        componentGrid.addComponentColumn("rate", addon -> createAddonRate(componentGrid.getComponentGridDecorator(), addon));
        componentGrid.addComponentColumn("delete", addon -> createAddonDelete(componentGrid.getComponentGridDecorator(), addon));
        componentGrid.setColumnOrder("seqNo", "type", "name","imagePath", "qtyArea", "rate", "amount", "delete");
        componentGrid.setColumns("seqNo", "type", "name", "imagePath", "qtyArea", "rate", "amount", "delete");

        HeaderRow mainHeader = componentGrid.getDefaultHeaderRow();
        mainHeader.getCell("seqNo").setText("#");
        mainHeader.getCell("type").setText("Type");
        mainHeader.getCell("name").setText("Name(Select)");
        mainHeader.getCell("imagePath").setText("Image");
        mainHeader.getCell("qtyArea").setText("Qty / Area");
        mainHeader.getCell("rate").setText("Rate");
        mainHeader.getCell("amount").setText("Amount");
        
        Image image = new Image();
        image.setSource(new ThemeResource("img/add_black.png"));
        image.setHeight("25px");
        image.setWidth("25px");
        mainHeader.getCell("delete").setComponent(image);
        
        image.addClickListener(new MouseEvents.ClickListener() 
        {
			@Override
			public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
				int newId = componentGrid.getContainerDataSource().size() + 1;
				Addon addon = new Addon();
				addon.setId(newId);
				addon.setImagePath("");
				addon.setAmount(0.00);
				addon.setRate(0.00);
				addon.setQtyArea(0);
				componentGrid.getContainerDataSource().addItem(addon);
				componentGrid.refresh();
			}
		});
        
        horizontalLayout0.addComponent(componentGrid);

        return horizontalLayout0;
    }

    public Component createAddonTypeSelector(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon, List<String> types) 
    {
        ComboBox select = new ComboBox();
        select.setWidth("150px");
        select.setHeight("30px");
        
        select.addItems(types);
        select.setPropertyDataSource(new BeanItem<>(addon).getItemProperty("type"));
        select.addValueChangeListener(e -> {
        	componentGridDecorator.refresh();
            System.out.println("" + e.getProperty().getValue().toString());
        });
        return select;
    }


    public Component createAddonNameSelector(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon, List<String> names) 
    {
        ComboBox select = new ComboBox();
        select.setWidth("150px");
        select.setHeight("30px");
        
        select.addItems(names);
        select.setPropertyDataSource(new BeanItem<>(addon).getItemProperty("name"));
        select.addValueChangeListener(e -> {
        	componentGridDecorator.refresh();
            System.out.println("" + e.getProperty().getValue().toString());
        });
        return select;
    }

    public Component createAddonImage(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon) 
    {
    	ExternalResource resource =
        		new ExternalResource(addon.getImagePath());
        Embedded image = new Embedded("", resource);
        image.setHeight(32, Sizeable.Unit.PIXELS);
        image.setWidth(32, Sizeable.Unit.PIXELS);
        return image;
    }

    public Component createAddonQty(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon) 
    {
        TextField qtyTxtField = new TextField("", addon.getQtyArea() + "");
        qtyTxtField.setWidth("40px");
        qtyTxtField.setHeight("30px");
        return qtyTxtField;
    }

    public Component createAddonRate(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon) 
    {
        TextField qtyTxtField = new TextField("", addon.getRate() + "");
        qtyTxtField.setWidth("60px");
        qtyTxtField.setHeight("30px");
        return qtyTxtField;
    }

    public Component createAddonDelete(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon) 
    {
    	HorizontalLayout actionLayout = new HorizontalLayout();
    	actionLayout.setSizeFull();
    	actionLayout.setSpacing(true);

        Image editImage = new Image();
        editImage.setSource(new ThemeResource("img/edit.png"));
        editImage.setWidth("25px");
        editImage.setHeight("25px");
        
        editImage.addClickListener(new MouseEvents.ClickListener() 
        {
			@Override
			public void click(com.vaadin.event.MouseEvents.ClickEvent event) 
			{
				componentGridDecorator.refresh();
			}
		});

        Image deleteImage = new Image();
        deleteImage.setSource(new ThemeResource("img/delete.png"));
        deleteImage.setWidth("25px");
        deleteImage.setHeight("25px");
        
        deleteImage.addClickListener(new MouseEvents.ClickListener() 
        {
			@Override
			public void click(com.vaadin.event.MouseEvents.ClickEvent event) 
			{
				componentGridDecorator.getGrid().getContainerDataSource().removeItem(addon);
				componentGridDecorator.refresh();
			}
		});
        
        actionLayout.addComponent(editImage);
        actionLayout.addComponent(deleteImage);
        return actionLayout;
    }

	private List<Addon> getAddon(String string) 
	{
		List<Addon> addons = new ArrayList<>(); 
		
        Addon addon = new Addon();
		addon.setSeqNo(1);
		addon.setId(1);
		addon.setType("ACCESSORY");
		addon.setName("GRANITE 4MX");
		addon.setDescription("");
		addon.setImagePath("https://www.funkit.net/2/2014/07/kitchen-granite-countertops-granite-at-beatiful-kitchen-island-minimalist-decor-ideas-brilliant-countertops-kitchen-options-eco-friendly-prefabricated-fake-zodiac-outlet-venetian-countertops-counter-tops-wils-options-for-1110x833.jpg");
	    addon.setQtyArea(20);
	    addon.setUom("");
	    addon.setRate(1000.00);
	    addon.setAmount(20000.00);
	    addons.add(addon);
	    
	    Addon addon2 = new Addon();
	    addon2.setSeqNo(2);
	    addon2.setId(2);
	    addon2.setType("COUNTER TOP");
	    addon2.setName("4T HANDLES");
	    addon2.setDescription("");
	    addon2.setImagePath("http://www.crystalhandle.com/images/cabinet_handles/cabinet_handle_od.jpg");
	    addon2.setQtyArea(8);
	    addon2.setUom("");
	    addon2.setRate(100.00);
	    addon2.setAmount(800.00);
	    addons.add(addon2);

	    Addon addon3 = new Addon();
	    addon3.setSeqNo(3);
	    addon3.setId(3);
	    addon3.setType("SERVICES");
	    addon3.setName("DADO");
	    addon3.setDescription("");
	    addon3.setImagePath("");
	    addon3.setQtyArea(4);
	    addon3.setUom("");
	    addon3.setRate(1500.00);
	    addon3.setAmount(6000.00);
	    addons.add(addon3);

		return addons;
	}

	/*
	 * Summary Section 
	 */
	
    private Component buildSummaryForm() 
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        
        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        verticalLayout.addComponent(horizontalLayout0);

        return verticalLayout;
    }

    /*
     * Attachment section
     */
    private Component buildAttachmentsForm() 
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        
        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        verticalLayout.addComponent(horizontalLayout0);

        return verticalLayout;
    }
    
    

    
    private Component buildFooter() 
    {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSizeFull();
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

    public static void open(Proposal proposal) {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Window w = new CustomizedItemDetailsWindow(proposal);
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    public static void close_window() {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
    }
    
    private ComboBox getSimpleItemFilledCombo(String caption, String dataType)
    {
        List<SimpleComboItem> list = proposalDataProvider.getComboItems(dataType);
        final BeanItemContainer<SimpleComboItem> container =
                new BeanItemContainer<SimpleComboItem>(SimpleComboItem.class);
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId("title");
    	SimpleComboItem selectedProductType = (container.size() > 0) ? container.getItemIds().get(0) : null;
    	select.setValue(selectedProductType);
    	return select; 
    }
}