package com.company;

public class AsmGuiResult {
    private String listing;
    private String binary;
    private String diag;

    public AsmGuiResult(String listing, String binary, String diag) {
        this.listing = listing;
        this.binary = binary;
        this.diag = diag;
    }

    public String getListing() {
        return listing;
    }

    public void setListing(String listing) {
        this.listing = listing;
    }

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public String getDiag() {
        return diag;
    }

    public void setDiag(String diag) {
        this.diag = diag;
    }
}
