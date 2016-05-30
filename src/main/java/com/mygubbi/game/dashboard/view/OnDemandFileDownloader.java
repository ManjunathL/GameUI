package com.mygubbi.game.dashboard.view;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by nitinpuri on 30-05-2016.
 */
public class OnDemandFileDownloader extends FileDownloader {

    /**
     * Provide both the {@link com.vaadin.server.StreamResource.StreamSource} and the filename in an on-demand way.
     */
    public interface OnDemandStreamResource extends StreamResource.StreamSource {
        String getFilename();
    }

    private static final long serialVersionUID = 1L;
    private final OnDemandStreamResource onDemandStreamResource;

    public OnDemandFileDownloader(OnDemandStreamResource onDemandStreamResource) {
        super(new StreamResource(onDemandStreamResource, ""));
        this.onDemandStreamResource = checkNotNull(onDemandStreamResource,
                "The given on-demand stream resource may never be null!");
    }

    @Override
    public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path)
            throws IOException {
        getResource().setFilename(onDemandStreamResource.getFilename());
        return super.handleConnectorRequest(request, response, path);
    }

    private StreamResource getResource() {
        return (StreamResource) this.getResource("dl");
    }

}