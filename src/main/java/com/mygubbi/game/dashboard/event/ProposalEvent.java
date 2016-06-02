package com.mygubbi.game.dashboard.event;

import com.mygubbi.game.dashboard.domain.AddonProduct;
import com.mygubbi.game.dashboard.domain.Module;
import com.mygubbi.game.dashboard.domain.Product;
import com.mygubbi.game.dashboard.domain.ProposalListViewItem;

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

        public ModuleUpdated(Module module) {
            this.module = module;
        }

        public Module getModule() {
            return module;
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

    public static class AddonUpdated {

        private final AddonProduct addonProduct;

        public AddonUpdated(AddonProduct addonProduct) {
            this.addonProduct = addonProduct;
        }

        public AddonProduct getAddonProduct() {
            return addonProduct;
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
