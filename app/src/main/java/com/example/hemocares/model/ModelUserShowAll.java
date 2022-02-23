package com.example.hemocares.model;

public class ModelUserShowAll {

    public String GUID, NOREG, FULLNAME, USERNAME, PASSWORD, EMAIL, PHONE, BLOOD_TYPE, ADDRESS, AGE, BIRTHDATE,
            STATUS, RELIGION, PHOTO, GENDER, ROLE, CREATED_AT, UPDATED_AT, RANGE;
    double LAT_LOCATION, LONG_LOCATION;

    public ModelUserShowAll(String guid, String noreg, String fullname, String username, String password, String email,
                            String phone, String blood_type, String address, String age, String birthdate,
                            String status, String religion, String photo,
                            String gender, String role, String created_at, String updated_at,
                            double latLocation, double longLocation, String range) {

        this.GUID = guid;
        this.NOREG = noreg;
        this.FULLNAME = fullname;
        this.USERNAME = username;
        this.PASSWORD = password;
        this.EMAIL = email;
        this.PHONE = phone;
        this.BLOOD_TYPE = blood_type;
        this.ADDRESS = address;
        this.AGE = age;
        this.BIRTHDATE = birthdate;
        this.STATUS = status;
        this.RELIGION = religion;
        this.PHOTO = photo;
        this.GENDER = gender;
        this.ROLE = role;
        this.CREATED_AT = created_at;
        this.UPDATED_AT = updated_at;
        this.LAT_LOCATION = latLocation;
        this.LONG_LOCATION = longLocation;
        this.RANGE = range;
    }

    public ModelUserShowAll() {

    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getNOREG() {
        return NOREG;
    }

    public void setNOREG(String NOREG) {
        this.NOREG = NOREG;
    }

    public String getFULLNAME() {
        return FULLNAME;
    }

    public void setFULLNAME(String FULLNAME) {
        this.FULLNAME = FULLNAME;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getBLOOD_TYPE() {
        return BLOOD_TYPE;
    }

    public void setBLOOD_TYPE(String BLOOD_TYPE) {
        this.BLOOD_TYPE = BLOOD_TYPE;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getAGE() {
        return AGE;
    }

    public void setAGE(String AGE) {
        this.AGE = AGE;
    }

    public String getBIRTHDATE() {
        return BIRTHDATE;
    }

    public void setBIRTHDATE(String BIRTHDATE) {
        this.BIRTHDATE = BIRTHDATE;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getRELIGION() {
        return RELIGION;
    }

    public void setRELIGION(String RELIGION) {
        this.RELIGION = RELIGION;
    }

    public String getPHOTO() {
        return PHOTO;
    }

    public void setPHOTO(String PHOTO) {
        this.PHOTO = PHOTO;
    }

    public String getGENDER() {
        return GENDER;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER;
    }

    public String getROLE() {
        return ROLE;
    }

    public void setROLE(String ROLE) {
        this.ROLE = ROLE;
    }

    public String getCREATED_AT() {
        return CREATED_AT;
    }

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

    public String getRANGE() {
        return RANGE;
    }

    public void setRANGE(String RANGE) {
        this.RANGE = RANGE;
    }
}
