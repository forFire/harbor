package com.capcare.harbor.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "us_vehicle")
public class VehicleBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1175814276874083L;

    //    `f_vehicle_id` varchar(64) NOT NULL DEFAULT '',
    //    `f_vehicle_model` varchar(32) DEFAULT NULL,
    //    `f_create_time` datetime DEFAULT NULL,
    //    `f_update_time` datetime DEFAULT NULL,

    @Id
    @Column(name = "f_vehicle_id")
    private String            vehicleId;

    @Column(name = "f_vehicle_model")
    private String            vehicleModel;

    @Column(name = "f_create_time")
    private Date              createTime;

    @Column(name = "f_update_time")
    private Date              updateTime;

    public String getVehicleId () {

        return vehicleId;
    }

    public void setVehicleId (String vehicleId) {

        this.vehicleId = vehicleId;
    }

    public String getVehicleModel () {

        return vehicleModel;
    }

    public void setVehicleModel (String vehicleModel) {

        this.vehicleModel = vehicleModel;
    }

    public Date getCreateTime () {

        return createTime;
    }

    public void setCreateTime (Date createTime) {

        this.createTime = createTime;
    }

    public Date getUpdateTime () {

        return updateTime;
    }

    public void setUpdateTime (Date updateTime) {

        this.updateTime = updateTime;
    }

}
