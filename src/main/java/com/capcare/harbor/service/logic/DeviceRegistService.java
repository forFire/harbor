package com.capcare.harbor.service.logic;

import java.util.Date;

import javax.annotation.Resource;

import module.util.MD5Util;
import module.util.Tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.dao.DeviceDao;
import com.capcare.harbor.dao.DeviceRegistDao;
import com.capcare.harbor.dao.VehicleDao;
import com.capcare.harbor.model.Device;
import com.capcare.harbor.model.DeviceRegisterBean;
import com.capcare.harbor.model.DeviceVehicle;
import com.capcare.harbor.model.VehicleBean;
import com.capcare.harbor.service.logic.bean.DeviceRegistRsBean;

@Component
@Scope("singleton")
public class DeviceRegistService {

    private Logger          log = LoggerFactory.getLogger (getClass ());

    @Resource
    private DeviceRegistDao registDao;

    @Resource
    private DeviceDao       deviceDao;

    @Resource
    private VehicleDao      vehicleDao;

    public DeviceRegistRsBean getRegistId (DeviceVehicle veh) {

        DeviceRegistRsBean bean = new DeviceRegistRsBean ();

        try {
//            VehicleBean vehicle = vehicleDao.getVehicle (veh.getPlate ());
//            if (vehicle == null) {
//                //                vehicle = new VehicleBean ();
//                //                vehicle.setCreateTime (DateUtil.nowDate ());
//                //                vehicle.setUpdateTime (DateUtil.nowDate ());
//                //                vehicle.setVehicleId (veh.getPlate ());
//                //                this.vehicleDao.saveBean (vehicle);
//                //                System.out.println ();
//                bean.setRegistRs (new Byte ("2"));//数据库中无该车辆
//                return bean;
//            }
            DeviceRegisterBean device=null;
            if(Tools.checkNotEmpty (veh.getPlate ())){
                
            
            device = this.registDao.getDeviceByPlat (veh.getPlate ());
            if (device != null && !device.getSn ().equals (veh.getDeviceId ())) {
                bean.setRegistRs (new Byte ("1"));//车辆已注册
                return bean;
            }
            }
            DeviceRegisterBean deviceBySn = this.registDao.getDeviceBySn (veh.getDeviceId ());
            if (deviceBySn != null && !deviceBySn.getPlate ().equals (veh.getPlate ())) {
                bean.setRegistRs (new Byte ("3"));//车辆已注册
                return bean;
            }
            Device deviceBean = this.deviceDao.get (veh.getDeviceId ());
            if (deviceBean == null) {
//                                deviceBean =new Device();
//                                deviceBean.setSn (veh.getDeviceId ());
//                                deviceBean.setType (0);
//                                deviceBean.setIsobd (Short.parseShort ("0"));
//                                deviceBean.setEnableSmsAlarm (1);
//                                deviceBean.setFeeCheckCmd ("");
//                                deviceBean.setEnableFeeCheck (1);
//                                deviceBean.setFeeCheckNo ("12");
//                                this.deviceDao.saveBean (deviceBean);
                bean.setRegistRs (new Byte ("4"));//设备未录入
                return bean;
            }
            if (device == null) {
                device = new DeviceRegisterBean ();
                device.setCityId (veh.getCityId ());
                device.setColor (veh.getColor ());
                device.setDeviceModel (veh.getDeviceModel ());
                device.setManufacturerId (veh.getManufacturerId ());
                device.setPlate (veh.getPlate ());
                device.setProvinceId (veh.getProvinceId ());
                device.setRegisterTime (new Date ());
                device.setRegistId (MD5Util.md5Byte32 (veh.getDeviceId () + "===" + device.getRegisterTime ().getTime ()));
                device.setSn (veh.getDeviceId ());
                registDao.saveOrUpdate (device);
                bean.setRegistRs (new Byte ("0"));
            }else{
                device.setRegisterTime (new Date ());
                device.setRegistId (MD5Util.md5Byte32 (veh.getDeviceId () + "===" + device.getRegisterTime ().getTime ()));
                registDao.saveOrUpdate (device);
                bean.setRegistRs (new Byte ("0"));
            }
            bean.setRegistId (device.getRegistId ());
        }
        catch (Exception e) {
            log.error ("", e);
        }
        return bean;
    }

    public String getRegistId (String sn) throws Exception {

        DeviceRegisterBean deviceBySn = this.registDao.getDeviceBySn (sn);
        if (deviceBySn == null) {
            log.error ("device didn't regist : {}", sn);
            throw new Exception ("device didn't regist");
        }
        return deviceBySn.getRegistId ();
    }

    public String getDeviceSn (String registId) {

        DeviceRegisterBean device = this.registDao.getDevice (registId);
        return device == null ? null : device.getSn ();
    }

    public boolean removeDevice (String deviceSn) {

        this.registDao.delDevice (deviceSn);
        return true;
    }

}
