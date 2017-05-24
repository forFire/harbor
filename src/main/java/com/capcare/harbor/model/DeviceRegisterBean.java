package com.capcare.harbor.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "us_device_register")
public class DeviceRegisterBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7231786595757360787L;

    @Id
    @Column(name = "f_sn", length = 28)
    private String            sn;

    @Column(name = "f_regist_id")
    private String            registId;

    @Column(name = "f_province_id")
    private int               provinceId;

    @Column(name = "f_city_id")
    private int               cityId;

    @Column(name = "f_manufacturer_id")
    private String            manufacturerId;

    @Column(name = "f_device_model")
    private String            deviceModel;

    @Column(name = "f_color")
    private int               color;

    @Column(name = "f_plate")
    private String            plate;

    @Column(name = "f_register_time")
    private Date              registerTime;

    public String getSn () {

        return sn;
    }

    public void setSn (String sn) {

        this.sn = sn;
    }

    public String getRegistId () {

        return registId;
    }

    public void setRegistId (String registId) {

        this.registId = registId;
    }

    public int getProvinceId () {

        return provinceId;
    }

    public void setProvinceId (int provinceId) {

        this.provinceId = provinceId;
    }

    public int getCityId () {

        return cityId;
    }

    public void setCityId (int cityId) {

        this.cityId = cityId;
    }

    public String getManufacturerId () {

        return manufacturerId;
    }

    public void setManufacturerId (String manufacturerId) {

        this.manufacturerId = manufacturerId;
    }

    public String getDeviceModel () {

        return deviceModel;
    }

    public void setDeviceModel (String deviceModel) {

        this.deviceModel = deviceModel;
    }

    public int getColor () {

        return color;
    }

    public void setColor (int color) {

        this.color = color;
    }

    public String getPlate () {

        return plate;
    }

    public void setPlate (String plate) {

        this.plate = plate;
    }

    public Date getRegisterTime () {

        return registerTime;
    }

    public void setRegisterTime (Date registerTime) {

        this.registerTime = registerTime;
    }

}
