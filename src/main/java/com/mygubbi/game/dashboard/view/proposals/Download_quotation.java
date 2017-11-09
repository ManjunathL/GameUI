package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinSession;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shruthi on 10/10/2017.
 */
public class Download_quotation extends Window
{
    private static final Logger LOG = LogManager.getLogger(Download_quotation.class);
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private ProposalHeader proposalHeader;
    private ProposalVersion proposalVersion;
    private Proposal proposal;
    private ProductAndAddonSelection productAndAddonSelection;
    List<Product> products = new ArrayList<>();
    List<AddonProduct> addons = new ArrayList<>();

    public Download_quotation(ProposalHeader proposalHeader, Proposal proposal,ProposalVersion proposalVersion)
    {
        this.proposalHeader=proposalHeader;
        this.proposal=proposal;
        this.proposalVersion=proposalVersion;
        this.products = proposalDataProvider.getVersionProducts(proposalHeader.getId(),proposalVersion.getVersion());
        this.addons = proposalDataProvider.getVersionAddons(proposalHeader.getId(),proposalVersion.getVersion());

        this.proposal.setProducts(this.products);
        this.proposal.setAddons(this.addons);
        this.productAndAddonSelection = new ProductAndAddonSelection();
        this.productAndAddonSelection.setProposalId(this.proposalHeader.getId());
        this.productAndAddonSelection.setFromVersion(this.proposalVersion.getVersion());
        this.productAndAddonSelection.setCity(proposalHeader.getPcity());
        DashboardEventBus.register(this);
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("35%");
        setHeight("20%");
        setClosable(false);

        setContent(buildHeading());
        DashboardEventBus.register(this);

    }
    private Component buildHeading() {
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.setSizeFull();
        verticalLayout1.setMargin(new MarginInfo(true,true,true,true));

        verticalLayout1.setSpacing(true);
        Double ver = Double.parseDouble(proposalVersion.getVersion());

        if(ver <= 1.0)
        {
            Label label_message = new Label();
            label_message.setCaption("Do you want to print Booking Form");
            label_message.addStyleName(ValoTheme.LABEL_BOLD);
            label_message.addStyleName(ValoTheme.LABEL_H2);
            verticalLayout1.addComponent(label_message);
        }
        else
        {
            Label label_message = new Label();
            label_message.setCaption("Do you want to print Works Contract");
            label_message.addStyleName(ValoTheme.LABEL_BOLD);
            label_message.addStyleName(ValoTheme.LABEL_H2);
            verticalLayout1.addComponent(label_message);
        }

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();

        Button quotePdf1 = new Button("Yes &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        quotePdf1.setCaptionAsHtml(true);
        quotePdf1.setIcon(FontAwesome.DOWNLOAD);
        quotePdf1.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        quotePdf1.addStyleName(ValoTheme.BUTTON_PRIMARY);
        quotePdf1.addStyleName(ValoTheme.BUTTON_SMALL);
        /*quotePdf1.addStyleName("margin-top-for-headerlevelbutton");
        quotePdf1.addStyleName("margin-right-10-for-headerlevelbutton");*/
        quotePdf1.setWidth("120px");
        quotePdf1.addClickListener(this::checkProductsAndAddonsAvailable1);

        StreamResource quotePdfresource = createQuoteResourcePdf();
        FileDownloader fileDownloaderPdf = new FileDownloader(quotePdfresource);
        fileDownloaderPdf.extend(quotePdf1);
        horizontalLayout1.addComponent(quotePdf1);

        Button quotePdf = new Button("No &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        quotePdf.setCaptionAsHtml(true);
        quotePdf.setIcon(FontAwesome.DOWNLOAD);
        quotePdf.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        quotePdf.addStyleName(ValoTheme.BUTTON_PRIMARY);
        quotePdf.addStyleName(ValoTheme.BUTTON_SMALL);
        /*quotePdf.addStyleName("margin-top-for-headerlevelbutton");
        quotePdf.addStyleName("margin-right-10-for-headerlevelbutton");*/
        quotePdf.setWidth("120px");
        quotePdf.addClickListener(this::checkProductsAndAddonsAvailable1);
        horizontalLayout1.addComponent(quotePdf);

        StreamResource quotePdfresourcewobookingform = createQuoteResourcePdfWoBookingForm();
        FileDownloader fileDownloaderPdfwobookingform = new FileDownloader(quotePdfresourcewobookingform);
        fileDownloaderPdfwobookingform.extend(quotePdf);
        horizontalLayout1.addComponent(quotePdf);


        Button closeBtn = new Button();
        closeBtn.setCaption("Close");
        closeBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        closeBtn.addStyleName(ValoTheme.BUTTON_SMALL);
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                DashboardEventBus.unregister(this);
                close();

            }
        });
        horizontalLayout1.addComponent(closeBtn);

        verticalLayout1.addComponent(horizontalLayout1);

        return verticalLayout1;

    }

    private Component buildHeadingButtons() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false, true, false, true));
        HorizontalLayout right = new HorizontalLayout();

        //newButton.addClickListener();
        Button quotePdf = new Button("No &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        quotePdf.setCaptionAsHtml(true);
        quotePdf.setIcon(FontAwesome.DOWNLOAD);
        quotePdf.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        quotePdf.addStyleName(ValoTheme.BUTTON_PRIMARY);
        quotePdf.addStyleName(ValoTheme.BUTTON_SMALL);
        quotePdf.addStyleName("margin-top-for-headerlevelbutton");
        quotePdf.addStyleName("margin-right-10-for-headerlevelbutton");
        quotePdf.setWidth("120px");
        quotePdf.addClickListener(this::checkProductsAndAddonsAvailable1);
        right.addComponent(quotePdf);
        right.setComponentAlignment(quotePdf, Alignment.MIDDLE_RIGHT);

        StreamResource quotePdfresourcewobookingform = createQuoteResourcePdfWoBookingForm();
        FileDownloader fileDownloaderPdfwobookingform = new FileDownloader(quotePdfresourcewobookingform);
        fileDownloaderPdfwobookingform.extend(quotePdf);
        right.addComponent(quotePdf);
        right.setComponentAlignment(quotePdf, Alignment.MIDDLE_RIGHT);

        Button quotePdf1 = new Button("Yes &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        quotePdf1.setCaptionAsHtml(true);
        quotePdf1.setIcon(FontAwesome.DOWNLOAD);
        quotePdf1.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        quotePdf1.addStyleName(ValoTheme.BUTTON_PRIMARY);
        quotePdf1.addStyleName(ValoTheme.BUTTON_SMALL);
        quotePdf1.addStyleName("margin-top-for-headerlevelbutton");
        quotePdf1.addStyleName("margin-right-10-for-headerlevelbutton");
        quotePdf1.setWidth("120px");
        quotePdf1.addClickListener(this::checkProductsAndAddonsAvailable1);

        StreamResource quotePdfresource = createQuoteResourcePdf();
        FileDownloader fileDownloaderPdf = new FileDownloader(quotePdfresource);
        fileDownloaderPdf.extend(quotePdf1);
        right.addComponent(quotePdf1);
        right.setComponentAlignment(quotePdf1, Alignment.MIDDLE_RIGHT);

        horizontalLayout.addComponent(right);
        horizontalLayout.setComponentAlignment(right, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
    }

    private void checkProductsAndAddonsAvailable1(Button.ClickEvent clickEvent) {
        if (proposal.getProducts().isEmpty() && proposal.getAddons().isEmpty()) {
            NotificationUtil.showNotification("No products found. Please add product(s) first to generate the Quote.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
        }
    }
    private StreamResource createQuoteResourcePdf() {
        StreamResource.StreamSource source = () -> {
            return getInputStreamPdf();
        };
        return new StreamResource(source, "Quotation.pdf");
    }

    private InputStream getInputStreamPdf() {
        productAndAddonSelection.setDiscountPercentage(proposalVersion.getDiscountPercentage());
        productAndAddonSelection.setDiscountAmount(proposalVersion.getDiscountAmount());
        productAndAddonSelection.setBookingFormFlag("No");
        productAndAddonSelection.setWorksContractFlag("No");

        if(proposalVersion.getVersion().startsWith("0.") || proposalVersion.getVersion().equals("1.0"))
        {
            productAndAddonSelection.setBookingFormFlag("Yes");
        }
        else
        {
            productAndAddonSelection.setWorksContractFlag("Yes");

        }
        /*if(!(proposalVersion.getVersion().equals("1.0")) && (proposalVersion.getVersion().startsWith("1.") || proposalVersion.getVersion().equals("2.0")))
        {
            productAndAddonSelection.setWorksContractFlag("Yes");
        }
        else
        {
            productAndAddonSelection.setWorksContractFlag("No");
        }*/

        productAndAddonSelection.setCity(proposalHeader.getPcity());
        String quoteFile = proposalDataProvider.getProposalQuoteFilePdf(this.productAndAddonSelection);

        InputStream input = null;
        try {
            input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(quoteFile)));
            DashboardEventBus.unregister(this);
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }
    private StreamResource createQuoteResourcePdfWoBookingForm()
    {
        StreamResource.StreamSource source = () -> {
            return getInputStreamPdfWoBookingForm();
        };
        return new StreamResource(source, "Quotation.pdf");
    }

    private InputStream getInputStreamPdfWoBookingForm() {
        productAndAddonSelection.setDiscountPercentage(proposalVersion.getDiscountPercentage());
        productAndAddonSelection.setDiscountAmount(proposalVersion.getDiscountAmount());
        /*if(proposalVersion.getVersion().startsWith("0.") || proposalVersion.getVersion().equals("1.0"))
        {
        */    productAndAddonSelection.setBookingFormFlag("No");
        productAndAddonSelection.setWorksContractFlag("No");
        /*}
        else
        {
            productAndAddonSelection.setBookingFormFlag("Yes");
        }*/
        /*if(!(proposalVersion.getVersion().equals("1.0")) && (proposalVersion.getVersion().startsWith("1.") || proposalVersion.getVersion().equals("2.0")))
        {
            productAndAddonSelection.setWorksContractFlag("No");
        }
        else
        {
            productAndAddonSelection.setWorksContractFlag("Yes");
        }*/
        productAndAddonSelection.setCity(proposalHeader.getPcity());
        String quoteFile = proposalDataProvider.getProposalQuoteFilePdf(this.productAndAddonSelection);

        InputStream input = null;
        try {
            input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(quoteFile)));
            DashboardEventBus.unregister(this);
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    public static void open(ProposalHeader proposalHeader,Proposal proposal,ProposalVersion proposalVersion)
    {
        Download_quotation w=new Download_quotation(proposalHeader,proposal,proposalVersion);
        UI.getCurrent().addWindow(w);
        w.focus();
    }


}
