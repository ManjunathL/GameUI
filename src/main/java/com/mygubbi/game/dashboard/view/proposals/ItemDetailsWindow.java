package com.mygubbi.game.dashboard.view.proposals;


import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ItemDetailsWindow extends Window {

    /**
     * Add Item Section
     */
/*    private TextField itemTitleField;
    private ComboBox productSelection;
    private ComboBox carcassMaterialSelection;
    private ComboBox colorSelection;
    
    private TextField typeField;
    private TextField quantityField;
    private ComboBox shutterMaterialSelection;
    
    private Upload uploadCtrl;
    private File uploadFile;
    private String uploadPath = "/tmp/";
    private ComboBox finishSelection;
    
    private Accordion accordion;
    private Button closeBtn;
    private Button saveBtn;
    
    private Proposal proposal;

    private final ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderMode());
    
    private ItemDetailsWindow(Proposal proposal) {

        this.proposal = proposal;
        
        setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
        setResizable(true);
        setClosable(false);
        setHeight(90.0f, Unit.PERCENTAGE);
        setWidth("800px");
        setCaption("Add Assembled Item");
        
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(new MarginInfo(true, false, false, false));
        
        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        horizontalLayout0.addComponent(buildAddItemBasicFormLeft());
        horizontalLayout0.addComponent(buildAddItemBasicFormRight());
        vLayout.addComponent(horizontalLayout0);
        vLayout.addComponent(new Label("</br>", ContentMode.HTML));
        vLayout.addComponent(buildUploadForm());
        
        accordion = new Accordion();
        accordion.addTab(buildModulesForm(), "Modules");
        accordion.addTab(buildAddonsForm(), "Addons");
        accordion.addTab(buildSummaryForm(), "Summary");
        accordion.addTab(buildAttachmentsForm(), "Attachments");
        accordion.setEnabled(true);
        vLayout.addComponent(accordion);
        
        vLayout.addComponent(new Label("</br></br>", ContentMode.HTML));
        vLayout.addComponent(buildFooter());
        
        setContent(vLayout);
        Responsive.makeResponsive(accordion);
    }

    *//*
     * Add Item basic section
     *//*
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

        this.shutterMaterialSelection = getSimpleItemFilledCombo("Shutter Material", "shutter_material_data");
        formLayoutLeft.addComponent(this.shutterMaterialSelection);

        return formLayoutLeft;
    }

    private FormLayout buildAddItemBasicFormRight() 
    {
        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.typeField = new TextField("Type");
        this.typeField.setValue("ASSEMBLED");
        this.typeField.setReadOnly(true);
        formLayoutRight.addComponent(typeField);

        this.quantityField = new TextField("Qty");
        formLayoutRight.addComponent(quantityField);

        this.colorSelection = getSimpleItemFilledCombo("Color", "color_data");
        formLayoutRight.addComponent(this.colorSelection);

        this.finishSelection = getSimpleItemFilledCombo("Finish", "finish_data");
    	formLayoutRight.addComponent(this.finishSelection);

        return formLayoutRight;
    }

    *//*
     * Upload section
     *//*
    private Component buildUploadForm()
    {
    	FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
         
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
		        accordion.setEnabled(true);
		        saveBtn.setVisible(true);
			}
		});

    	this.uploadCtrl.addFailedListener(new Upload.FailedListener() 
    	{
			@Override
			public void uploadFailed(FailedEvent event) {
				System.out.println("Error upload - "+ event.getFilename() + " :" + event.getReason().getMessage());
			}
		});
    	formLayout.addComponent(this.uploadCtrl);
    	
    	
        return formLayout;
    }
    
    *//*
     * Modules Section
     *//*
    List<String> carcassMaterialL = new ArrayList<>();
    List<String> shutterMaterialL = new ArrayList<>();
    List<String> finishL = new ArrayList<>();
    List<String> colorL = new ArrayList<>();
    Map<String, String> mgModuleMap = new HashMap<>();
    
    private Component buildModulesForm() 
    {
        VerticalLayout vLayout = new VerticalLayout();
        
        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setSizeFull();

        List<LookupItem> carcassMaterials = proposalDataProvider.getLookupItems("color_data");
        carcassMaterials.forEach(item -> carcassMaterialL.add(item.title));
        
        List<LookupItem> shutterMaterials = proposalDataProvider.getLookupItems("shutter_material_data");
        shutterMaterials.forEach(item -> shutterMaterialL.add(item.title));
        
        List<LookupItem> finishes = proposalDataProvider.getLookupItems("finish_data");
        finishes.forEach(item -> finishL.add(item.title));
        
        List<LookupItem> colors = proposalDataProvider.getLookupItems("color_data");
        colors.forEach(item -> colorL.add(item.title));
        
        ComponentGrid<Module> componentGrid = new ComponentGrid<>(Module.class);
        componentGrid.setHeight("200px");
        componentGrid.setWidth("1000px");
        
        componentGrid.setRows(getModules(""));
        componentGrid.setDetailsGenerator(new ModulesDetailGenerator());
        
        componentGrid.addComponentColumn("importStatus", module -> createModuleStatus(componentGrid.getComponentGridDecorator(), module));
        
        componentGrid.addComponentColumn("mgModuleCode", module -> 
        							createMgModuleSelector(componentGrid.getComponentGridDecorator(), module, mgModuleMap.keySet()));
        
        componentGrid.addComponentColumn("image", module -> createModuleImage(componentGrid.getComponentGridDecorator(), module));
        
        componentGrid.addComponentColumn("carcassMaterial", module -> createModuleCM(componentGrid.getComponentGridDecorator(), module));
        componentGrid.addComponentColumn("shutterMaterial", module -> createModuleSM(componentGrid.getComponentGridDecorator(), module));
        componentGrid.addComponentColumn("finish", module -> createModuleFinish(componentGrid.getComponentGridDecorator(), module));
        componentGrid.addComponentColumn("color", module -> createModuleColor(componentGrid.getComponentGridDecorator(), module));
        
        componentGrid.setColumnOrder("importStatus", "seqNo", "importedModuleCode", "image", "mgModuleCode", "carcassMaterial", "shutterMaterial", "finish", "color", "qty", "amount");
        componentGrid.setColumns("importStatus", "seqNo", "importedModuleCode", "image", "mgModuleCode", "carcassMaterial", "shutterMaterial", "finish", "color", "qty", "amount");
        
        HeaderRow mainHeader = componentGrid.getDefaultHeaderRow();
        mainHeader.getCell("importStatus").setText("");
        mainHeader.getCell("seqNo").setText("#");
        mainHeader.getCell("importedModuleCode").setText("Imported Module");
        mainHeader.getCell("mgModuleCode").setText("Mg Module");
        mainHeader.getCell("carcassMaterial").setText("Carcass Material");
        mainHeader.getCell("shutterMaterial").setText("Shutter Material");
        mainHeader.getCell("finish").setText("Finish");
        mainHeader.getCell("color").setText("Color");
        mainHeader.getCell("qty").setText("Qty");
        mainHeader.getCell("amount").setText("Amount");

        
        hLayout.addComponent(componentGrid);
        vLayout.addComponent(hLayout);

        return vLayout;
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
        select.addItems(shutterMaterialL);
        select.setPropertyDataSource(new BeanItem<>(module).getItemProperty("shutterMaterial"));
        select.addValueChangeListener(e -> 
        {
        	componentGridDecorator.refresh();
        });
        return select;
	}

    private Component createModuleFinish(ComponentGridDecorator<Module> componentGridDecorator, Module module) 
    {
        ComboBox select = new ComboBox();
        select.setWidth("150px");
        select.setHeight("30px");
        select.addItems(finishL);
        select.setPropertyDataSource(new BeanItem<>(module).getItemProperty("finish"));
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
            module, Set<String> mgModules) 
    {
        ComboBox select = new ComboBox();
        select.setWidth("150px");
        select.setHeight("30px");
        select.addItems(mgModules);
        select.setPropertyDataSource(new BeanItem<>(module).getItemProperty("mgModuleCode"));
        select.addValueChangeListener(e -> 
        {
            int index = componentGridDecorator.getGrid().getContainerDataSource().indexOfId(module);
            System.out.println("" + e.getProperty().getValue().toString() + ":" + index);
        	
            componentGridDecorator.getGrid().getContainerDataSource().removeItem(module);
        	module.setImagePath(mgModuleMap.get(e.getProperty().getValue()));
        	componentGridDecorator.getGrid().getContainerDataSource().addItemAt(index, module);
        	componentGridDecorator.refresh();
        });
        return select;
    }
    
    private List<Module> getModules(String string) 
    {
        mgModuleMap.put("K2DU4572", "http://ecx.images-amazon.com/images/I/5182QOOr2oL._SS40_.jpg");
        mgModuleMap.put("K2DU4571", "http://ecx.images-amazon.com/images/I/41n4ib0WvdL._SS36_.jpg");
        mgModuleMap.put("SINK1072", "http://i.ebayimg.com/images/g/N9QAAOxyBjBTWAe1/s-l64.jpg");
    	
    	List<Module> moduleL = new ArrayList<>(); 
    	Module module = new Module();
    	module.setSeq(1);
    	module.setExtCode("ADD1_536");
        module.setMgCode("K2DU4572");
        module.setDescription("Kitche 2 drawer unit");
        module.setWidth(440.100);
        module.setDepth(100.00);
        module.setHeight(200.00);
        module.setImagePath(mgModuleMap.get("K2DU4572"));
        module.setBaseCarcass("PLY");
        module.setCarcassMaterialId(1);
        module.setShutterMaterial("MDF");
        module.setShutterMaterialId(5);
        module.setFinish("20191 HGL FROSTY WHITE");
        module.setFinishId(1);
        module.setColor("Forsty Brown");
        module.setQuantity(1);
        module.setAmount(1300.00);
        module.setImportStatus(ImportStatusType.MATCHED);
        moduleL.add(module);
        
        Module module2 = new Module();
        module2.setSeq(2);
        module2.setExtCode("FUL1_536");
        module2.setMgCode("K2DU4571");
        module2.setDescription("Kitche 2 drawer unit");
        module2.setWidth(440.100);
        module2.setDepth(100.00);
        module2.setHeight(200.00);
        module2.setImagePath(mgModuleMap.get("K2DU4571"));
        module2.setBaseCarcass("PLY");
        module2.setCarcassMaterialId(2);
        module2.setShutterMaterial("MDF");
        module2.setShutterMaterialId(6);
        module2.setFinish("20192 HGL FROSTY WHITE");
        module2.setFinishId(1);
        module2.setColor("Forsty White");
        module2.setQuantity(2);
        module2.setAmount(2600.00);
        module2.setImportStatus(ImportStatusType.DEFAULT_MATCHED);
        moduleL.add(module2);
        
        Module module3 = new Module();
        module3.setSeq(3);
        module3.setExtCode("FUL1_537");
        module3.setMgCode("");
        module3.setDescription("Kitche 2 drawer unit");
        module3.setWidth(440.100);
        module3.setDepth(100.00);
        module3.setHeight(200.00);
        module3.setImagePath("");
        module3.setBaseCarcass("PLY");
        module3.setCarcassMaterialId(2);
        module3.setShutterMaterial("MDF");
        module3.setShutterMaterialId(6);
        module3.setFinish("20192 HGL FROSTY WHITE");
        module3.setFinishId(1);
        module3.setColor("Forsty Brown");
        module3.setQuantity(2);
        module3.setAmount(2600.00);
        module3.setImportStatus(ImportStatusType.NOT_MATCHED);
        moduleL.add(module3);
        
		return moduleL;
	}

    *//*
     * Addons section
     *//*
    
	private Component buildAddonsForm() 
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        
        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        verticalLayout.addComponent(horizontalLayout0);

        List<String> addonTypes = new ArrayList<>();
        addonTypes.add("ACCESSORY");
        addonTypes.add("COUNTER TOP");
        addonTypes.add("SERVICES");
        
        List<String> addonNameL = new ArrayList<>();
        addonNameL.add("4T HANDLES");
        addonNameL.add("GRANITE 4MX");
        addonNameL.add("DADO");

        ComponentGrid<Addon> componentGrid = new ComponentGrid<>(Addon.class);
        componentGrid.setHeight("200px");
        componentGrid.setWidth("1000px");
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
        image.setSource(new ThemeResource("img/add.png"));
        mainHeader.getCell("delete").setComponent(image);
        
        
        horizontalLayout0.addComponent(componentGrid);

        return verticalLayout;
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
        Image image = new Image();
        image.setSource(new ThemeResource("img/delete.png"));
        image.setWidth("25px");
        image.setHeight("25px");
        
        image.addClickListener(new MouseEvents.ClickListener() 
        {
			@Override
			public void click(com.vaadin.event.MouseEvents.ClickEvent event) 
			{
				System.out.println("Deleting row");
				componentGridDecorator.getGrid().getContainerDataSource().removeItem(addon);
				componentGridDecorator.refresh();
			}
		});
        return image;
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

	*//*
     * Summary Section
	 *//*
	
    private Component buildSummaryForm() 
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        
        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        verticalLayout.addComponent(horizontalLayout0);

        return verticalLayout;
    }

    *//*
     * Attachment section
     *//*
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
        Window w = new ItemDetailsWindow(proposal);
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    public static void close_window() {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
    }
    
    private ComboBox getSimpleItemFilledCombo(String caption, String dataType)
    {
        List<LookupItem> list = proposalDataProvider.getLookupItems(dataType);
        final BeanItemContainer<LookupItem> container =
                new BeanItemContainer<LookupItem>(LookupItem.class);
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId("title");
    	LookupItem selectedProductType = (container.size() > 0) ? container.getItemIds().get(0) : null;
    	select.setValue(selectedProductType);
    	return select; 
    }*/
}