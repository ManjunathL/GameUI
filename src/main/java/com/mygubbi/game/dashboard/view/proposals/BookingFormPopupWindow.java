package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.ProductAndAddonSelection;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Shruthi on 8/7/2017.
 */
public class BookingFormPopupWindow extends Window
{
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private static final Logger LOG = LogManager.getLogger(BookingFormPopupWindow.class);
    String optionValue;

    ProductAndAddonSelection productAndAddonSelection;
    public BookingFormPopupWindow(ProductAndAddonSelection productAndAddonSelection)
    {
        //this.bookingForm=bookingForm;
        this.productAndAddonSelection=productAndAddonSelection;
        LOG.info(productAndAddonSelection.toString());
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("35%");
        setHeight("20%");
        setClosable(false);
        setContent(buildMainWindow());
        DashboardEventBus.register(this);
    }
    private Component buildMainWindow()
    {
        LOG.info("build window call");
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.setSizeFull();
        verticalLayout1.setMargin(new MarginInfo(true,true,true,true));

        verticalLayout1.setSpacing(true);

        Label label_message = new Label();
        label_message.setCaption("Please click on Yes to print booking form");
        label_message.addStyleName(ValoTheme.LABEL_BOLD);
        label_message.addStyleName(ValoTheme.LABEL_H2);
        verticalLayout1.addComponent(label_message);

        OptionGroup optionGroup=new OptionGroup();
        optionGroup.addItems("Yes","No");
        optionGroup.addStyleName("horizontal");
        optionGroup.setRequired(true);
        optionGroup.select("No");
        optionGroup.setImmediate(true);
        productAndAddonSelection.setBookingFormFlag("No");
        optionGroup.addValueChangeListener(this::optiongroupValueChange);
        verticalLayout1.addComponent(optionGroup);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();

        Button quotePdf = new Button("Quote Pdf&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        quotePdf.setCaptionAsHtml(true);
        quotePdf.setIcon(FontAwesome.DOWNLOAD);
        quotePdf.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        quotePdf.addStyleName(ValoTheme.BUTTON_PRIMARY);
        quotePdf.addStyleName(ValoTheme.BUTTON_SMALL);
        /*quotePdf.addStyleName("margin-top-for-headerlevelbutton");
        quotePdf.addStyleName("margin-right-10-for-headerlevelbutton");*/
        quotePdf.setWidth("120px");

        quotePdf.addClickListener(this::checkProductsAndAddonsAvailable);

        StreamResource quotePdfresource = createQuoteResourcePdf();
        FileDownloader fileDownloaderPdf = new FileDownloader(quotePdfresource);
        fileDownloaderPdf.extend(quotePdf);
        horizontalLayout1.addComponent(quotePdf);
        horizontalLayout1.setComponentAlignment(quotePdf, Alignment.MIDDLE_CENTER);

        Button ok=new Button("Close");
        ok.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        horizontalLayout1.addComponent(ok);
        horizontalLayout1.setComponentAlignment(ok,Alignment.MIDDLE_CENTER);
        ok.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                DashboardEventBus.unregister(this);
                close();
            }
        });

        verticalLayout1.addComponent(horizontalLayout1);
        return verticalLayout1;
    }
    private void  optiongroupValueChange(Property.ValueChangeEvent valueChangeEvent)
    {
        LOG.info("value in value change event " +valueChangeEvent.getProperty().getValue().toString());
        optionValue=valueChangeEvent.getProperty().getValue().toString();
        productAndAddonSelection.setBookingFormFlag(valueChangeEvent.getProperty().getValue().toString());
    }
    public static void open(ProductAndAddonSelection productAndAddons)
    {
        Window w = new BookingFormPopupWindow(productAndAddons);
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    private void checkProductsAndAddonsAvailable(Button.ClickEvent clickEvent) {

    }
    private StreamResource createQuoteResourcePdf() {
        StreamResource.StreamSource source = () ->
        {
                InputStream input = null;
                LOG.info("option value " +optionValue);

                    LOG.info("inside if");
                    LOG.info("products and addons " +productAndAddonSelection);
                    String quoteFile = proposalDataProvider.getProposalQuoteFilePdf(this.productAndAddonSelection);
                    LOG.info("quote file " + quoteFile);

                    try {
                        input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(quoteFile)));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                   /* value = "";*/
                    return input;

            };
        return new StreamResource(source,"Quotation.pdf");

    }
}
