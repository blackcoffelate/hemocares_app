package com.example.hemocares.model;

public class ModelUserReportAll {

    public String GUID, CONTENT, REPORT_TYPE, ADDRESS, PHOTO_CONTENT, FULLNAME, BLOOD_TYPE, STATUS, PHOTO, CREATED_AT, UPDATED_AT;
    double LAT_LOCATION, LONG_LOCATION;

    public ModelUserReportAll(String guid, String fullname, String blood_type, String content, String report_type,
                              String status, String photo, String photo_content,
                              String created_at, String updated_at,
                              double latLocation, double longLocation) {

        this.GUID = guid;
        this.PHOTO_CONTENT = photo_content;
        this.CONTENT = content;
        this.REPORT_TYPE = report_type;
        this.FULLNAME = fullname;
        this.BLOOD_TYPE = blood_type;
        this.STATUS = status;
        this.PHOTO = photo;
        this.CREATED_AT = created_at;
        this.UPDATED_AT = updated_at;
        this.LAT_LOCATION = latLocation;
        this.LONG_LOCATION = longLocation;
    }

    public ModelUserReportAll() {

    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getCONTENT() {
        return CONTENT;
    }

    public void setCONTENT(String CONTENT) {
        this.CONTENT = CONTENT;
    }

    public String getREPORT_TYPE() {
        return REPORT_TYPE;
    }

    public void setREPORT_TYPE(String REPORT_TYPE) {
        this.REPORT_TYPE = REPORT_TYPE;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getPHOTO_CONTENT() {
        return PHOTO_CONTENT;
    }

    public void setPHOTO_CONTENT(String PHOTO_CONTENT) {
        this.PHOTO_CONTENT = PHOTO_CONTENT;
    }

    public String getFULLNAME() {
        return FULLNAME;
    }

    public void setFULLNAME(String FULLNAME) {
        this.FULLNAME = FULLNAME;
    }

    public String getBLOOD_TYPE() {
        return BLOOD_TYPE;
    }

    public void setBLOOD_TYPE(String BLOOD_TYPE) {
        this.BLOOD_TYPE = BLOOD_TYPE;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getPHOTO() {
        return PHOTO;
    }

    public void setPHOTO(String PHOTO) {
        this.PHOTO = PHOTO;
    }

    public String getCREATED_AT() { return CREATED_AT; }

    public void setCREATED_AT(String CREATED_AT) {
        this.CREATED_AT = CREATED_AT;
    }

    public String getUPDATED_AT() {
        return UPDATED_AT;
    }

    public void setUPDATED_AT(String UPDATED_AT) {
        this.UPDATED_AT = UPDATED_AT;
    }

    public double getLAT_LOCATION() {
        return LAT_LOCATION;
    }

    public void setLAT_LOCATION(double LAT_LOCATION) {
        this.LAT_LOCATION = LAT_LOCATION;
    }

    public double getLONG_LOCATION() {
        return LONG_LOCATION;
    }

    public void setLONG_LOCATION(double LONG_LOCATION) {
        this.LONG_LOCATION = LONG_LOCATION;
    }
}
