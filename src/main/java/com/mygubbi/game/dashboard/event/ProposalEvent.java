package com.mygubbi.game.dashboard.event;

import com.mygubbi.game.dashboard.domain.*;
import com.vaadin.ui.Window;

/**
 * Created by nitinpuri on 04-05-2016.
 */
public abstract class ProposalEvent {

    public static class ProposalUpdated {
    }

    public static class ListItemAdded {
        private ProposalListViewItem proposalListViewItem;
        public ListItemAdded(ProposalListViewItem proposalListViewItem) {
            this.proposalListViewItem = proposalListViewItem;
        }
        public ProposalListViewItem getProposalListViewItem() {
            return proposalListViewItem;
        }
    }

    public static class ModuleUpdated {
        private Module module;
        private boolean loadNext;
        private boolean loadPrevious;
        private int moduleIndex;
        private Window window;

        public ModuleUpdated(Module module, boolean loadNext, boolean loadPrevious, int moduleIndex, Window window) {
            this.module = module;
            this.loadNext = loadNext;
            this.loadPrevious = loadPrevious;
            this.moduleIndex = moduleIndex;
            this.window = window;

        }

        public Window getWindow() {
            return window;
        }

        public Module getModule() {
            return module;
        }

        public boolean isLoadNext() {
            return loadNext;
        }

        public boolean isLoadPrevious() { return loadPrevious;}

        public int getModuleIndex() {
            return moduleIndex;
        }
    }

    public static class ModuleCreated {
        private Module module;
        private boolean loadNext;
        private boolean loadPrevious;
        private int moduleIndex;
        private Window window;

        public ModuleCreated(Module module, boolean loadNext, boolean loadPrevious, int moduleIndex, Window window) {
            this.module = module;
            this.loadNext = loadNext;
            this.loadPrevious = loadPrevious;
            this.moduleIndex = moduleIndex;
            this.window = window;

        }

        public Window getWindow() {
            return window;
        }

        public Module getModule() {
            return module;
        }

        public boolean isLoadNext() {
            return loadNext;
        }

        public boolean isLoadPrevious() { return loadPrevious;}

        public int getModuleIndex() {
            return moduleIndex;
        }
    }



    public static class ProductCreatedOrUpdatedEvent {
        private Product product;
        public ProductCreatedOrUpdatedEvent(Product product) {
            this.product = product;
        }

        public Product getProduct() {
            return product;
        }
    }

    public static class VersionCreated {
        private ProposalVersion proposalVersion;
        public VersionCreated(ProposalVersion proposalVersion) {
            this.proposalVersion = proposalVersion;
        }

        public ProposalVersion getProposalVersion() {
            return proposalVersion;
        }
    }

    public static class DashboardMenuUpdated {
        boolean status;
        public DashboardMenuUpdated(boolean status) {
            this.status = status;
        }
             public boolean getDashboardMenuStatus() {
            return status;
        }
    }

    public static class AddonUpdated {

        private final AddonProduct addonProduct;

        public AddonUpdated(AddonProduct addonProduct) {
            this.addonProduct = addonProduct;
        }

        public AddonProduct getAddonProduct() {
            return addonProduct;
        }
    }

    public static class ProposalAddonUpdated extends AddonUpdated {

        public ProposalAddonUpdated(AddonProduct addonProduct) {
            super(addonProduct);
        }
    }

    public static class ProductDeletedEvent {
        private final Product product;

        public ProductDeletedEvent(Product product) {
            this.product = product;
        }

        public Product getProduct() {
            return product;
        }
    }
}
