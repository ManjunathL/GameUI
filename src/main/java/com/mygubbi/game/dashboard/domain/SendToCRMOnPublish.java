package com.mygubbi.game.dashboard.domain;

/**
 * Created by User on 08-05-2017.
 */
public class SendToCRMOnPublish {


    public static final String CRM_ID= "opportunity_name";
    public static final String ESTIMATED_PROJECT_COST = "estimated_project_cost_c";
    public static final String QUOTE_NO = "quotation_number_c";
    public static final String QUOTE_LINK = "quotelink";
    public static final String DESIGNER_NAME = "designer_name";
    public static final String DESIGNER_EMAIL = "designer_email";

    private String opportunity_name;
    private double estimated_project_cost_c;
    private String quotation_number_c;
    private String quotelink;
    private String designer_name;
    private String designer_email;

    public String getOpportunity_name() {
        return opportunity_name;
    }

    public void setOpportunity_name(String opportunity_name) {
        this.opportunity_name = opportunity_name;
    }

    public double getEstimated_project_cost_c() {
        return estimated_project_cost_c;
    }

    public void setEstimated_project_cost_c(double estimated_project_cost_c) {
        this.estimated_project_cost_c = estimated_project_cost_c;
    }

    public String getQuotation_number_c() {
        return quotation_number_c;
    }

    public void setQuotation_number_c(String quotation_number_c) {
        this.quotation_number_c = quotation_number_c;
    }

    public String getQuotelink() {
        return quotelink;
    }

    public void setQuotelink(String quotelink) {
        this.quotelink = quotelink;
    }

    public String getDesigner_name() {
        return designer_name;
    }

    public void setDesigner_name(String designer_name) {
        this.designer_name = designer_name;
    }

    public String getDesigner_email() {
        return designer_email;
    }

    public void setDesigner_email(String designer_email) {
        this.designer_email = designer_email;
    }

    @Override
    public String toString() {
        return "SendToCRMOnPublish{" +
                "opportunity_name='" + opportunity_name + '\'' +
                ", estimated_project_cost_c=" + estimated_project_cost_c +
                ", quotation_number_c='" + quotation_number_c + '\'' +
                ", quotelink='" + quotelink + '\'' +
                ", designer_name='" + designer_name + '\'' +
                ", designer_email='" + designer_email + '\'' +
                '}';
    }
}


