package com.capcare.harbor.model;

public class VersionBean {

    private String version;
    private String fileName;
    private String hardWare;
    private String type;

    public String getVersion () {

        return version;
    }

    public void setVersion (String version) {

        this.version = version;
    }

    public String getFileName () {

        return fileName;
    }

    public void setFileName (String fileName) {

        this.fileName = fileName;
    }

    public String getHardWare () {

        return hardWare;
    }

    public void setHardWare (String hardWare) {

        this.hardWare = hardWare;
    }

    public String getType () {

        return type;
    }

    public void setType (String type) {

        this.type = type;
    }

}
