package com.capcare.harbor.handler.device.m2616;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import module.util.JsonUtils;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capcare.harbor.model.VersionBean;
import com.capcare.harbor.util.Helper;
import com.capcare.harbor.vo.InstructVo;
import com.capcare.harbor.vo.InstructType;

/**
 * 设备指令 协议转换类 <br/>
 * 
 * 由凯步协议 转换为 设备协议
 * @author capcare
 *
 */
public class M2616Encoder {

    private static final String default_pwd = "0000";
    private static final Logger logger      = LoggerFactory.getLogger (M2616Encoder.class);

    /**
     *  将服务器端指令 转换为 为设备需要的格式
     *  
     * @param session
     * @param protocol
     * @return
     * @throws Exception
     */
    public static List <String> encodeInstruct (IoSession session, InstructVo protocol) throws Exception {

        Map <String, Object> cmdMap = protocol.getCmdMap ();
        List <String> downList = new LinkedList <String> ();

        InstructType instructType = protocol.getInstructType ();
        String downStr = null;
        String [] params = null;

        logger.info("------instructType--------"+instructType);
        
        switch (instructType) {
        case SetTickInterval:// 设置上传时间间隔
            String interval = "30";
            String count = "1";
            if (cmdMap.containsKey ("interval")) {
                interval = String.valueOf ((Integer) cmdMap.get ("interval"));
            }
            if (cmdMap.containsKey ("count")) {
                count = String.valueOf ((Integer) cmdMap.get ("count"));
            }
            params = new String [] {
                    interval, count
            };
            downStr = M2616Encoder.encode (instructType, params);
            downList.add (downStr);
            break;

        case SetFence:// 设置围栏
            String fenceId = "1";
            int type = 1;// 围栏类型，默认为圆形
            if (cmdMap.get ("type") != null)
                type = (Integer) cmdMap.get ("type");

            if (type == 1) {// 圆形

                Double [] center = (Double []) cmdMap.get ("center");
                String lat = null;
                String lng = null;
                if (center != null) {
                    lat = String.valueOf (center [1]);
                    lng = String.valueOf (center [0]);
                }

                String radius = null;
                if (cmdMap.get ("radius") != null)
                    radius = String.valueOf ((Integer) cmdMap.get ("radius"));
                params = new String [] {
                        fenceId, String.valueOf (type), lng, lat, radius, null
                };
                downStr = M2616Encoder.encode (InstructType.SetFence, params);
                downList.add (downStr);
            }
            else if (type == 2) {// 矩形
                String region = ((String) cmdMap.get ("region")).replaceAll ("lat=", "\"lat\":").replaceAll ("lng=",
                        "\"lng\":");
                @SuppressWarnings("unchecked")
                List <Map <Double, Double>> list = JsonUtils.json2Obj (region, ArrayList.class);
                if (list != null) {
                    Map <Double, Double> point1 = list.get (3);
                    Map <Double, Double> point2 = list.get (1);
                    String lng1 = String.valueOf (point1.get ("lng"));
                    String lat1 = String.valueOf (point1.get ("lat"));
                    String lng2 = String.valueOf (point2.get ("lng"));
                    String lat2 = String.valueOf (point2.get ("lat"));
                    params = new String [] {
                            fenceId, String.valueOf (type), lng1, lat1, lng2, lat2
                    };
                    downStr = M2616Encoder.encode (InstructType.SetFence, params);
                    downList.add (downStr);
                }
            }
            String condition = "1";
            if (cmdMap.get ("condition") != null) {
                int conInt = (Integer) cmdMap.get ("condition");
                if (conInt == 1)
                    condition = String.valueOf (2);
            }
            params = new String [] {
                    fenceId, "1", condition
            };
            downStr = M2616Encoder.encode (InstructType.SetFenceStatus, params);
            downList.add (downStr);

            params = new String [] {
                "1"
            };
            downStr = M2616Encoder.encode (InstructType.SetFenceSwitch, params);
            downList.add (downStr);
            break;

        case SetSpeed:// 超速报警
            int spdSwitch = 1;
            spdSwitch = (Integer) cmdMap.get ("switch");
            if (spdSwitch != 1)
                spdSwitch = 0;
            String min = String.valueOf (cmdMap.get ("min"));
            String max = String.valueOf (cmdMap.get ("max"));

            params = new String [] {
                    String.valueOf (spdSwitch), min, max
            };
            downStr = M2616Encoder.encode (InstructType.SetSpeed, params);
            downList.add (downStr);
            break;
        case SetSos:// 设置管理人号码
            String [] nums = (String []) cmdMap.get ("nums");

            for (int i = 0; i < 3; i++) {
                String num = "0";
                if (i < nums.length)
                    num = nums [i].trim ();
                try {
                    if (Long.parseLong (num) == 0)
                        num = "";
                }
                catch (Exception e) {
                    logger.error ("encodeInstruct", e);
                    num = "";
                }
                params = new String [] {
                        String.valueOf (i + 1), num
                };
                downStr = M2616Encoder.encode (InstructType.SetSos, params);
                downList.add (downStr);
            }

            break;
        case Reboot:// 重启
            downStr = M2616Encoder.encode (InstructType.Reboot, null);
            downList.add (downStr);
            break;

        case Restore:// 恢复出厂设置

            params = new String [] {
                    "30", "1"
            };
            downStr = M2616Encoder.encode (InstructType.SetTickInterval, params);
            downList.add (downStr);

            params = new String [] {
                    "1", "0", "120"
            };
            downStr = M2616Encoder.encode (InstructType.SetSpeed, params);
            downList.add (downStr);

            params = new String [] {
                "0"
            };
            downStr = M2616Encoder.encode (InstructType.SetFenceSwitch, params);
            downList.add (downStr);

            params = new String [] {
                    "0", "0"
            };
            downStr = M2616Encoder.encode (InstructType.SetMoveSwitch, params);
            downList.add (downStr);

            break;

        case SetFenceSwitch:// 围栏报警开关
            int fenceSwitch = 1;
            fenceSwitch = (Integer) cmdMap.get ("switch");
            if (fenceSwitch != 1)
                fenceSwitch = 0;

            params = new String [] {
                String.valueOf (fenceSwitch)
            };
            downStr = M2616Encoder.encode (InstructType.SetFenceSwitch, params);
            downList.add (downStr);
            break;

        case SetMoveSwitch:// 移动报警开关
            int moveSwitch = (Integer) cmdMap.get ("switch");
            int sense = 2;
            int duration = 6;
            if (moveSwitch == 2) {
                sense = 0;
                duration = 0;
            }

            params = new String [] {
                    String.valueOf (sense), String.valueOf (duration)
            };
            downStr = M2616Encoder.encode (InstructType.SetMoveSwitch, params);
            downList.add (downStr);
            break;
        case Upgrade://升级
            //            String deviceSn = cmdMap.get ("deviceSn").toString ();
            VersionBean  versionBean = (VersionBean )cmdMap.get ("version");
            String version = versionBean.getVersion ().toString ();
            downStr = encode (InstructType.Upgrade, new String [] {
                    version, Helper.get ("upgrade.url"), Helper.get ("upgrade.port")
            });
//            downStr = downStr.replace ("0000##", "");
            downList.add (downStr);
        default:
            break;
        }

        return downList;
    }

    /**
     * 取得 清除告警 指令
     * @param session
     * @param protocol
     */
    public static String getClearAlarmCmd () {

        String downStr = M2616Encoder.encode (InstructType.ClearAlarm, null);
        return downStr;
    }

    private static String encode (InstructType instructType, String [] params) {

    	
    	 logger.info("------encode--------"+instructType);
    	
        StringBuilder content = new StringBuilder ();

        content.append ("#");
        content.append (InstructMapping.mapToDevice (instructType));
        content.append ("#");
        if (params != null && params.length > 0) {
            for (String param : params) {
                if (param != null && !"".equals (param)) {
                    content.append (param);
                }
                content.append ("#");
            }
        }
        content.append (default_pwd);
        content.append ("##");
        return content.toString ();
    }
}
