package com.mygubbi.game.dashboard.view;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.FileAttachment;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.domain.User;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ClickableRenderer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.gridutil.renderer.ViewDeleteButtonValueRenderer;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Created by nitinpuri on 30-05-2016.
 */
public class FileAttachmentComponent extends VerticalLayout {

    private static final Logger LOG = LogManager.getLogger(FileAttachmentComponent.class);
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();

    private final SaveAttachmentsListener saveListener;
    private final DeleteAttachmentsListener deleteListener;

    private TextField fileTitle;
    private BeanItemContainer<FileAttachment> attachmentContainer;
    private Grid attachmentGrid;
    private Button prevDownloadButton;
    private FileAttachmentsHolder attachmentsHolder;
    private final String uploadBasePath;
    private Upload fileUploadCtrl;
    private boolean readOnly;
    private ComboBox fileType;

    public FileAttachmentComponent(FileAttachmentsHolder attachmentsHolder, String uploadBasePath,
                                   SaveAttachmentsListener saveListener, DeleteAttachmentsListener deleteListener,
                                   boolean readOnly) {
        this.attachmentsHolder = attachmentsHolder;
        this.uploadBasePath = uploadBasePath;
        this.saveListener = saveListener;
        this.deleteListener = deleteListener;
        this.readOnly = readOnly;
        buildAttachmentsForm();
    }

    private Component buildAttachmentsForm() {
        VerticalLayout verticalLayout = this;

        HorizontalLayout fileUploadHLayout = new HorizontalLayout();
        FormLayout left = new FormLayout();
        fileTitle = new TextField("Title");
        fileTitle.setRequired(true);
        left.addComponent(fileTitle);
        fileUploadHLayout.addComponent(left);

        FormLayout middle = new FormLayout();
        fileType = getFileTypeCombo();
        fileType.setRequired(true);
        middle.addComponent(fileType);
        fileUploadHLayout.addComponent(middle);

        FormLayout right = new FormLayout();
        right.addComponent(getFileUploadControl());
        fileUploadHLayout.addComponent(right);
        verticalLayout.addComponent(fileUploadHLayout);

        HorizontalLayout fileGridHLayout = new HorizontalLayout();
        fileGridHLayout.setSizeFull();

        attachmentContainer = new BeanItemContainer<>(FileAttachment.class);

        GeneratedPropertyContainer genContainer = createGeneratedFAPropertyContainer();

        attachmentGrid = new Grid(genContainer);
        attachmentGrid.setSizeFull();
        attachmentGrid.setHeight("280px");
        attachmentGrid.setColumnReorderingAllowed(true);
        attachmentGrid.setColumns(FileAttachment.SEQ, FileAttachment.TITLE, FileAttachment.TYPE, FileAttachment.FILENAME, FileAttachment.UPLOADED_BY, FileAttachment.UPLOADED_ON, "action");

        List<Grid.Column> columns = attachmentGrid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("#");
        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Type");
        columns.get(idx++).setHeaderCaption("File Name");
        columns.get(idx++).setHeaderCaption("Uploaded By");
        columns.get(idx++).setHeaderCaption("Uploaded On");
        Grid.Column actionColumn = columns.get(idx);
        actionColumn.setHeaderCaption("Action");
        actionColumn.setRenderer(new ViewDeleteButtonValueRenderer(new ViewDeleteButtonValueRenderer.ViewDeleteButtonClickListener() {

            @Override
            public void onView(ClickableRenderer.RendererClickEvent rendererClickEvent) {
                if (prevDownloadButton != null) {
                    fileUploadHLayout.removeComponent(prevDownloadButton);
                }
                String currentAttachmentFile = ((FileAttachment) rendererClickEvent.getItemId()).getFileName();
                StreamResource myResource = createResource(currentAttachmentFile);
                double randomId = Math.random();
                prevDownloadButton = new Button("dummy" + randomId);
                prevDownloadButton.setStyleName("invisible-btn");
                prevDownloadButton.setId("btn-" + randomId);
                FileDownloader fileDownloader = new FileDownloader(myResource);
                fileDownloader.extend(prevDownloadButton);
                fileUploadHLayout.addComponent(prevDownloadButton);

                Page.getCurrent().getJavaScript()
                        .execute("document.getElementById('btn-" + randomId + "').click();");
            }

            @Override
            public void onDelete(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                if (readOnly) {
                    NotificationUtil.showNotification("This operation is allowed only in 'Draft' state.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
                } else {
                    ConfirmDialog.show(UI.getCurrent(), "", "Do you want to delete the attachment PERMANENTLY?",
                            "Yes", "No", dialog -> {
                                if (!dialog.isCanceled()) {
                                    Object itemId = rendererClickEvent.getItemId();

                                    attachmentsHolder.getFileAttachmentList().remove(itemId);

                                    int seq = ((FileAttachment) itemId).getSeq();

                                    attachmentContainer.removeAllItems();

                                    for (FileAttachment attachment : attachmentsHolder.getFileAttachmentList()) {
                                        if (attachment.getSeq() > seq) {
                                            attachment.setSeq(attachment.getSeq() - 1);
                                        }
                                    }

                                    attachmentContainer.addAll(attachmentsHolder.getFileAttachmentList());
                                    attachmentGrid.setContainerDataSource(createGeneratedFAPropertyContainer());
                                    if (deleteListener != null) fireDeleteEvent((FileAttachment) itemId);

                                }
                            });
                }
            }
        }));

        fileGridHLayout.addComponent(attachmentGrid);
        fileGridHLayout.setExpandRatio(attachmentGrid, 1);

        if (!attachmentsHolder.getFileAttachmentList().isEmpty()) {
            int i = 1;
            for (FileAttachment attachment : attachmentsHolder.getFileAttachmentList()) {
                attachment.setSeq(i++);
            }
            attachmentContainer.addAll(attachmentsHolder.getFileAttachmentList());
            attachmentGrid.sort(FileAttachment.SEQ, SortDirection.ASCENDING);
        }
        verticalLayout.addComponent(fileGridHLayout);
        verticalLayout.setExpandRatio(fileGridHLayout, 1.0f);

        return verticalLayout;
    }

    private ComboBox getFileTypeCombo() {
        List<LookupItem> list = proposalDataProvider.getLookupItems(ProposalDataProvider.ATTACHMENT_TYPE_LOOKUP);
        final BeanContainer<String, LookupItem> container =
                new BeanContainer<>(LookupItem.class);
        container.setBeanIdProperty(LookupItem.TITLE);
        container.addAll(list);

        ComboBox select = new ComboBox("&nbsp;&nbsp;&nbsp;Type");
        select.setCaptionAsHtml(true);
        select.setWidth("300px");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(LookupItem.TITLE);

        return select;

    }

    private GeneratedPropertyContainer createGeneratedFAPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(attachmentContainer);
        genContainer.addGeneratedProperty("action", getFileActionTextGenerator());
        return genContainer;
    }

    private boolean fireDeleteEvent(FileAttachment itemId) {
        AttachmentData attachmentData = new AttachmentData();
        attachmentData.setAttachmentsHolder(attachmentsHolder);
        attachmentData.setFileAttachment(itemId);
        return deleteListener.onDelete(attachmentData);
    }

    private StreamResource createResource(String currentAttachmentFile) {
        StreamResource.StreamSource source = () -> {
            InputStream input = null;
            try {
                input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(uploadBasePath + "/" + currentAttachmentFile)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return input;
        };
        return new StreamResource(source, currentAttachmentFile);
    }

    private PropertyValueGenerator<String> getFileActionTextGenerator() {
        return new PropertyValueGenerator<String>() {

            @Override
            public String getValue(Item item, Object o, Object o1) {
                return "";
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        };
    }

    private Component getFileUploadControl() {
        this.fileUploadCtrl = new Upload("&nbsp;&nbsp;&nbsp;Upload File", (filename, mimeType) -> {

            if (StringUtils.isEmpty(filename)) {
                NotificationUtil.showNotification("Please specify the file.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return null;
            }

            if (StringUtils.isEmpty(fileTitle.getValue()) || StringUtils.isEmpty((String) fileType.getValue())) {
                NotificationUtil.showNotification("Please fill the title & type before upload!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return null;
            }

            FileOutputStream fos = null;
            File uploadedFile = new File(uploadBasePath + "/" + filename);
            uploadedFile.getParentFile().mkdirs();
            try {
                fos = new FileOutputStream(uploadedFile);
            } catch (final FileNotFoundException e) {
                NotificationUtil.showNotification("Please specify the file path correctly.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
            return fos;
        });

        fileUploadCtrl.setCaptionAsHtml(true);
        fileUploadCtrl.setStyleName("upload-btn");
        fileUploadCtrl.addProgressListener((Upload.ProgressListener) (readBytes, contentLength) -> LOG.debug("Upload file Progress " + (readBytes * 100 / contentLength)));
        fileUploadCtrl.addSucceededListener((Upload.SucceededListener) event -> {

            FileAttachment fileAttachment = new FileAttachment();
            fileAttachment.setFileName(event.getFilename());
            fileAttachment.setSeq(attachmentsHolder.getFileAttachmentList().size() + 1);
            fileAttachment.setTitle(fileTitle.getValue());
            fileAttachment.setAttachmentType(fileType.getValue().toString());
            fileAttachment.setUploadedBy(getUserId());
            fileAttachment.setUploadedOn(new Date());

            attachmentsHolder.getFileAttachmentList().add(fileAttachment);
            attachmentContainer.addItem(fileAttachment);
            attachmentGrid.sort(FileAttachment.SEQ, SortDirection.ASCENDING);

            fileTitle.setValue("");

            if (saveListener != null) fireAddEvent(fileAttachment);
        });
        return fileUploadCtrl;
    }

    private void fireAddEvent(FileAttachment fileAttachment) {
        AttachmentData attachmentData = new AttachmentData();
        attachmentData.setAttachmentsHolder(attachmentsHolder);
        attachmentData.setFileAttachment(fileAttachment);
        saveListener.onSave(attachmentData);
    }

    private String getUserId() {
        return ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getEmail();
    }

    public static interface SaveAttachmentsListener {
        boolean onSave(AttachmentData attachmentData);
    }

    public static interface DeleteAttachmentsListener {
        boolean onDelete(AttachmentData attachmentData);
    }

    public static class AttachmentData {
        private FileAttachmentsHolder attachmentsHolder;
        private FileAttachment fileAttachment;

        public FileAttachmentsHolder getAttachmentsHolder() {
            return attachmentsHolder;
        }

        public void setAttachmentsHolder(FileAttachmentsHolder attachmentsHolder) {
            this.attachmentsHolder = attachmentsHolder;
        }

        public FileAttachment getFileAttachment() {
            return fileAttachment;
        }

        public void setFileAttachment(FileAttachment fileAttachment) {
            this.fileAttachment = fileAttachment;
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Upload getFileUploadCtrl() {
        return fileUploadCtrl;
    }
}
