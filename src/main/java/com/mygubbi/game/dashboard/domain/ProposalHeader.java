package com.mygubbi.game.dashboard.domain;

import java.util.Date;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class ProposalHeader {

    private String proposalTitle;
    private String proposalVersion;
    private String crmId;
    private String quotationNo;

    private CustomerDetails customerDetails;
    private ProjectDetails projectDetails;
    private MGContact salesContact;
    private MGContact designContact;

    private class CustomerDetails {
        private String customerId;
        private String customerName;
        private String addressLine1;
        private String addressLine2;
        private String addressLine3;
        private String city;
        private String email;
        private String phone1;
        private String phone2;

    }

    private class ProjectDetails {
        private String projectName;
        private String modelType;
        private String drawingNo;
        private String addressLine1;
        private String addressLine2;
        private String city;
    }

    private class MGContact {
        private String name;
        private String email;
        private String phone;
    }
}
