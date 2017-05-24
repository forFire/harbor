package com.capcare.harbor.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import module.util.JsonUtils;
import module.util.Tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.capcare.harbor.StartDeviceHarbor;
import com.capcare.harbor.dao.InstructDao;
import com.capcare.harbor.model.Fence;
import com.capcare.harbor.model.Instruct;
import com.capcare.harbor.model.ShortMessage;
import com.capcare.harbor.model.SpeetInstructVo;
import com.capcare.harbor.model.VersionBean;
import com.capcare.harbor.service.logic.InstructService;
import com.capcare.harbor.service.logic.ShortMessageService;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.InstructVo;

public class InstructConvertor {

    private static Logger log = LoggerFactory.getLogger (InstructConvertor.class);

    /**
     * 下发指令:上传间隔
     */
    private static InstructVo tick (String deviceSn, int tick) {

        InstructVo protocol = new InstructVo ();// 下发指令:上传间隔
        protocol.setDeviceSn (deviceSn);
        protocol.setInstructType (InstructType.SetTickInterval);

        Map <String, Object> cmdObj = new HashMap <String, Object> ();
        cmdObj.put ("interval", tick);
        protocol.setCmdMap (cmdObj);
        return protocol;
    }

    /**
     * 下发指令:sos号码
     */
    private static InstructVo sos (String deviceSn, String sosNum) {

        InstructVo protocol = new InstructVo ();// 下发指令:sos号码
        protocol.setDeviceSn (deviceSn);
        protocol.setInstructType (InstructType.SetSos);

        Map <String, Object> cmdObj = new HashMap <String, Object> ();
        String [] sosNums = sosNum.split (",");
        cmdObj.put ("nums", sosNums);
        cmdObj.put ("numStr", sosNum);
        protocol.setCmdMap (cmdObj);
        return protocol;
    }

    /**
     * 下发指令:超速 最大值-超速开/关
     */
    private static InstructVo hb (String deviceSn, int open, int max) {

        InstructVo protocol = new InstructVo ();// 下发指令:超速最大值
        protocol.setDeviceSn (deviceSn);
        protocol.setInstructType (InstructType.SetSpeed);

        Map <String, Object> cmdObj = new HashMap <String, Object> ();
        cmdObj.put ("switch", open);
        cmdObj.put ("min", 0);
        cmdObj.put ("max", max);
        protocol.setCmdMap (cmdObj);
        return protocol;
    }

    /**
     * 下发指令:围栏
     */
    private static InstructVo fence (String deviceSn, Fence formFence) {

        InstructVo protocol = new InstructVo ();// 下发指令
        protocol.setDeviceSn (deviceSn);
        protocol.setInstructType (InstructType.SetFence);

        Map <String, Object> cmdObj = new HashMap <String, Object> ();
        cmdObj.put ("condition", formFence.getOut ());
        cmdObj.put ("type", formFence.getType ());
        if (Fence.TYPE_CIRCLE == formFence.getType ()) {
            cmdObj.put ("center", formFence.getCenter ());
            cmdObj.put ("radius", formFence.getRadius ());
        }
        else if (Fence.TYPE_POLYGON == formFence.getType ()) {
            cmdObj.put ("region", JsonUtils.obj2Str (formFence.getRegion ()));
        }
        else {
            throw new IllegalArgumentException ("围栏类型错误:" + formFence.getType ());
        }
        protocol.setCmdMap (cmdObj);
        return protocol;
    }

    /**
     * 下发指令:重启
     */
    private static InstructVo reset (String deviceSn) {

        InstructVo protocol = new InstructVo ();// 下发指令
        protocol.setDeviceSn (deviceSn);
        protocol.setInstructType (InstructType.Reboot);
        return protocol;
    }

    /**
     * 下发指令:恢复出厂
     */
    private static InstructVo restore (String deviceSn) {

        InstructVo protocol = new InstructVo ();// 下发指令
        protocol.setDeviceSn (deviceSn);
        protocol.setInstructType (InstructType.Restore);
        return protocol;
    }

    /**
     * 下发指令:围栏开关  old
     */
    private static InstructVo fenceSwitch (String deviceSn, int fenceSwitch) {

        InstructVo protocol = new InstructVo ();// 下发指令:上传间隔

        protocol.setDeviceSn (deviceSn);
        protocol.setInstructType (InstructType.SetFenceSwitch);
        Map <String, Object> cmdMap = new HashMap <String, Object> ();
        cmdMap.put ("switch", fenceSwitch);

        protocol.setCmdMap (cmdMap);
        return protocol;
    }

    /**
    * 下发指令:围栏开关  old
    */
    private static InstructVo fenceSwitch (String deviceSn, int fenceSwitch, Fence formFence) {

        InstructVo protocol = new InstructVo ();// 下发指令:上传间隔

        protocol.setDeviceSn (deviceSn);
        protocol.setInstructType (InstructType.SetFenceSwitch);
        Map <String, Object> cmdMap = new HashMap <String, Object> ();
        cmdMap.put ("switch", fenceSwitch);
        if (formFence == null) {
            formFence = new Fence ();
            formFence.setOut (1);
            formFence.setType (Fence.TYPE_CIRCLE);
            formFence.setCenter (new Double [] {
                    0d, 0d
            });
            formFence.setRadius (1);
        }
        cmdMap.put ("condition", formFence.getOut ());
        cmdMap.put ("type", formFence.getType ());
        if (Fence.TYPE_CIRCLE == formFence.getType ()) {
            cmdMap.put ("center", formFence.getCenter ());
            cmdMap.put ("radius", formFence.getRadius ());
        }
        else if (Fence.TYPE_POLYGON == formFence.getType ()) {
            cmdMap.put ("region", JsonUtils.obj2Str (formFence.getRegion ()));
        }
        else {
            throw new IllegalArgumentException ("围栏类型错误:" + formFence.getType ());
        }

        protocol.setCmdMap (cmdMap);
        return protocol;
    }

    /**
     * 下发指令:移动开关
     */
    private static InstructVo moveSwitch (String deviceSn, int moveSwitch) {

        InstructVo protocol = new InstructVo ();// 下发指令:上传间隔
        protocol.setDeviceSn (deviceSn);
        protocol.setInstructType (InstructType.SetMoveSwitch);

        Map <String, Object> cmdObj = new HashMap <String, Object> ();
        cmdObj.put ("switch", moveSwitch);

        protocol.setCmdMap (cmdObj);
        return protocol;
    }

    /**
     * 下发指令:清除故障码
     */
    private static InstructVo clearOBDError (String deviceSn) {

        InstructVo protocol = new InstructVo ();// 下发指令
        protocol.setDeviceSn (deviceSn);
        protocol.setInstructType (InstructType.ClearOBDError);
        return protocol;
    }

    private static InstructVo fenceSwitch (Instruct instruct, String deviceSn) {

        //t808协议需要fence信息上面是老版本指令，为兼容老版本下发指令
        try {
            //老版本下发指令
            return fenceSwitch (deviceSn, Integer.parseInt (instruct.getContent ()));
        }
        catch (Exception e) {
            Map <String, Object> str2Map = JsonUtils.str2Map (instruct.getContent ());
            int switchType = Tools.toInt (str2Map.get ("switch"));
            Object fence = str2Map.get ("fence");

            try {
                return fenceSwitch (deviceSn, switchType, JsonUtils.str2Obj (JsonUtils.getJson (fence), Fence.class));
            }
            catch (Exception e1) {
                log.error ("", e1);
                return null;
            }
        }
    }

    private static InstructVo upgrade (Instruct instruct, String deviceSn) {

        InstructVo instructVo = new InstructVo ();
        instructVo.setDeviceSn (deviceSn);
        instructVo.setInstructType (InstructType.Upgrade);

        VersionBean bean = JsonUtils.str2Obj (instruct.getContent (), VersionBean.class);

        Map <String, Object> cmdObj = new HashMap <String, Object> ();
        cmdObj.put ("deviceSn", deviceSn);
        cmdObj.put ("version", bean);
        instructVo.setCmdMap (cmdObj);
        return instructVo;

    }

    /**
                  * 下发指令
                  * 
                  * 上线时读取离线指令
                  */
    public static InstructVo instruct (Instruct instruct, String deviceSn) {

        int type = instruct.getType ();
        switch (type) {
        case 1:
            Fence formFence = null;
            String value = instruct.getContent ();
            formFence = JsonUtils.str2Obj (value, Fence.class);
            return fence (deviceSn, formFence);
        case 2:
            return tick (deviceSn, Integer.parseInt (instruct.getContent ()));
        case 3:
            SpeetInstructVo siv = null;
            siv = JsonUtils.str2Obj (instruct.getContent (), SpeetInstructVo.class);
            return hb (deviceSn, siv.getOpen (), siv.getMax ());
        case 4:
            return sos (deviceSn, instruct.getContent ());
        case 5:
            return reset (deviceSn);
        case 6:
            return restore (deviceSn);
        case 7:
            return fenceSwitch (instruct, deviceSn);
        case 8:
            return moveSwitch (deviceSn, Integer.parseInt (instruct.getContent ()));
        case 11:
            return clearOBDError (deviceSn);
        case 12:
            return upgrade (instruct, deviceSn);
        case 2001://短报文
            return ShortMessage (instruct, deviceSn);
        default:
            throw new IllegalArgumentException ("指令类型错误");
        }
    }

    private static InstructVo ShortMessage (Instruct instruct, String deviceSn) {

        InstructVo instructVo = new InstructVo ();
        instructVo.setDeviceSn (deviceSn);
        instructVo.setInstructType (InstructType.SentMessage);

        ClassPathXmlApplicationContext context = StartDeviceHarbor.getInstance ().getContext ();
        ShortMessageService sMessageService = context.getBean (ShortMessageService.class);
        InstructDao instructDao = context.getBean (InstructDao.class);
        //TODO 先取一个
        List <ShortMessage> shortMessages = sMessageService.findNotSendSuccess (deviceSn);
        ShortMessage shortMessage = shortMessages.get (0);
        instruct.setReply (1);
        instructDao.saveOrUpdate (instruct);
//        //TODO instruct 置1 shortMessage status置1
//        shortMessage.setStatus (1);
//        sMessageService.saveOrUpdate (shortMessage);
        
        Map <String, Object> cmdObj = new HashMap <String, Object> ();
        cmdObj.put ("toSn", shortMessage.getReceiveHandsetId ());
        cmdObj.put ("content", shortMessage.getSendContent ());
        instructVo.setCmdMap (cmdObj);
        return instructVo;
    }
}
