package com.capcare.harbor.handler.device.t808;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import module.util.BytesConvert;
import module.util.JsonUtils;
import module.util.Tools;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.PhoneItem;
import com.capcare.harbor.service.cache.InstructCache;
import com.capcare.harbor.service.logic.bean.Item;
import com.capcare.harbor.vo.FenceVo;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.PointVo;
import com.capcare.harbor.vo.SpotVo;

@SuppressWarnings("unchecked")
@Component
@Scope("singleton")
public class T808EncoderUtils {

    private static BytesConvert convert;

    @Resource
    private T808CodeUtils       codeUtils;

    private Logger              log = LoggerFactory.getLogger (getClass ());

    @Resource
    private InstructCache       instructCache;

    public T808EncoderUtils toMsgSecquent (T808Message msg, IoSession session) {

        msg.setMessageSequnce (BytesConvert.int2Bytes (codeUtils.addMsgNo (session), 2));
        return this;
    }

    public T808EncoderUtils toMsgId (T808Message msg, int msgId) {

        msg.setMessageId (BytesConvert.int2Bytes (msgId, 2));
        return this;
    }

    public void setTickInterval (Map <String, Object> cmdMap, T808Message message, String deviceSn, int instructType) {

        Object interval = cmdMap.get ("interval");
        int intervalInt = Tools.checkEmpty (interval) ? 0 : Tools.toInt (interval);
        //设置了定时汇报，并设置驾驶员未登录汇报时间间隔、休眠时汇报时间间隔、紧急报警时汇报时间间隔、缺省汇报时间间隔
        message.setMessageBody (BytesConvert.concatAll (new byte [] {
            5
        }, BytesConvert.int2Bytes (0x0020, 4), new byte [] {
                4, 0, 0, 0, 0
        }, BytesConvert.int2Bytes (0x0022, 4), new byte [] {
            4
        }, BytesConvert.int2Bytes (intervalInt, 4), BytesConvert.int2Bytes (0x0027, 4), new byte [] {
            4
        }, BytesConvert.int2Bytes (intervalInt, 4), BytesConvert.int2Bytes (0x0028, 4), new byte [] {
            4
        }, BytesConvert.int2Bytes (intervalInt, 4), BytesConvert.int2Bytes (0x0029, 4), new byte [] {
            4
        }, BytesConvert.int2Bytes (intervalInt, 4)

        ));
        this.instructCache
                .setSequenceNo (deviceSn, BytesConvert.bytes2Int (message.getMessageSequnce ()), instructType);
    }

    @SuppressWarnings("static-access")
    public void setDeviceParam (Map <String, Object> cmdMap, T808Message message, String deviceSn, int instructType) {

        List <Item> all_params = (List <Item>) cmdMap.get ("all_params");

        byte [] countAll = new byte [] {
            (byte) all_params.size ()
        };
        List <byte []> bytes = new ArrayList <byte []> ();
        for (Item param : all_params) {
            getDeviceParam (param, bytes);
        }
        byte [] concatAll = convert.concatAll (countAll, bytes);
        message.setMessageBody (concatAll);
        this.instructCache.setSequenceNo (deviceSn, BytesConvert.bytes2Int (message.getMessageSequnce ()),
                InstructType.SetSos.getNum ());
    }

    @SuppressWarnings("static-access")
    public void getDeviceParam (Item param, List <byte []> bytes) {

        int param_id = param.getId ();
        Object param_value = param.getValue ();

        switch (param_id) {
        case 0x0001:
        case 0x0002:
        case 0x0003:
        case 0x0004:
        case 0x0005:
        case 0x0006:
        case 0x0007:
        case 0x0018:
        case 0x0019:
        case 0x0022:
        case 0x0021:
        case 0x0020:
        case 0x0027:
        case 0x0028:
        case 0x0029:
        case 0x002C:
        case 0x002D:
        case 0x002E:
        case 0x002F:
        case 0x0030:
        case 0x0045:
        case 0x0046:
        case 0x0047:
        case 0x0050:
        case 0x0051:
        case 0x0052:
        case 0x0053:
        case 0x0054:
        case 0x0055:
        case 0x0056:
        case 0x0057:
        case 0x0058:
        case 0x0059:
        case 0x005A:
        case 0x0070:
        case 0x0071:
        case 0x0072:
        case 0x0073:
        case 0x0074:
        case 0x0080:
            bytes.add (convert.concatAll (convert.int2Bytes (param_id, 4), convert.int2Bytes (4, 1),
                    convert.int2Bytes (Tools.toInt (param_value), 4))); // id + 长度 + 值
            break;
        case 0x0081:
        case 0x0082:
        case 0x0031:
            bytes.add (convert.concatAll (convert.int2Bytes (param_id, 4), convert.int2Bytes (2, 1),
                    convert.int2Bytes (Tools.toInt (param_value), 2))); // id + 长度 + 值
            break;
        case 0x0084:
            bytes.add (convert.concatAll (convert.int2Bytes (param_id, 4), convert.int2Bytes (1, 1),
                    convert.int2Bytes (Tools.toInt (param_value), 1))); // id + 长度 + 值
            break;
        default:
            byte [] values = String.valueOf (param_value).getBytes ();
            bytes.add (convert.concatAll (convert.int2Bytes (param_id, 4), convert.int2Bytes (values.length, 1), values)); // id + 长度 + 值
            break;
        }
    }

    public void setEvent (Map <String, Object> cmdMap, T808Message message) {

        // 设置类型
        int etype = (Integer) cmdMap.get ("event_type");
        // 事件项列表
        List <Item> all_events = (List <Item>) cmdMap.get ("all_events");
        // 事件总数
        int event_num = all_events.size ();

        message.setBodyLength (2);
        message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (etype, 1),
                BytesConvert.int2Bytes (event_num, 1)));

        // 事件项列表
        for (Item event : all_events) {
            // 事件ID
            int event_id = event.getId ();
            // 事件内容
            String event_content = (String) event.getValue ();
            // 事件内容长度
            int event_length = event_content.length ();

            message.setBodyLength (message.getBodyLength () + event_length + 2);

            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                    BytesConvert.int2Bytes (event_id, 1), BytesConvert.int2Bytes (event_length, 1),
                    event_content.getBytes ()));
        }
    }

    public void issueQuestion (Map <String, Object> cmdMap, T808Message message) {

        // 标志
        int qmark = (Integer) cmdMap.get ("mark");
        // 问题
        String question = (String) cmdMap.get ("question");
        // 问题内容长度
        int question_length = question.length ();

        message.setBodyLength (question_length + 2);
        message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (), BytesConvert.int2Bytes (qmark, 1),
                BytesConvert.int2Bytes (question_length, 1), question.getBytes ()));

        // 候选答案列表
        List <Item> answer_list = (List <Item>) cmdMap.get ("answer_list");
        for (Item answer : answer_list) {
            // 答案ID
            int answer_id = (Integer) answer.getId ();
            // 答案内容 STRING
            String answer_content = (String) answer.getValue ();
            // 答案内容长度 WORD
            int answer_length = answer_content.length ();

            message.setBodyLength (message.getBodyLength () + answer_length + 3);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                    BytesConvert.int2Bytes (answer_id, 1), BytesConvert.int2Bytes (answer_length, 2),
                    answer_content.getBytes ()));
        }
    }

    public void setDemandMenu (Map <String, Object> cmdMap, T808Message message) {

        int menu_type = (Integer) cmdMap.get ("menu_type");
        List <Item> menu_list = (List <Item>) cmdMap.get ("menu_list");
        int menu_num = menu_list.size ();

        message.setBodyLength (2);
        message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                BytesConvert.int2Bytes (menu_type, 1), BytesConvert.int2Bytes (menu_num, 1)));

        for (Item menu : menu_list) {
            int menu_id = menu.getId ();
            String menu_content = menu.getValue ();
            int menu_length = menu_content.length ();

            message.setBodyLength (message.getBodyLength () + menu_length + 2);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                    BytesConvert.int2Bytes (menu_id, 1), BytesConvert.int2Bytes (menu_length, 2),
                    menu_content.getBytes ()));
        }
    }

    public void setPhoneBook (Map <String, Object> cmdMap, T808Message message) {

        // 设置类型
        int phone_type = (Integer) cmdMap.get ("phone_type");
        // 联系人项
        List <PhoneItem> user_list = (List <PhoneItem>) cmdMap.get ("user_list");

        // 联系人总数
        int total_user = user_list.size ();

        message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (phone_type, 1),
                BytesConvert.int2Bytes (total_user, 1)));

        for (PhoneItem user : user_list) {
            // 0 标志 BYTE
            int call_type = user.getId ();

            // 2 电话号码 STRING
            String phone_number = user.getPhone ();

            // 1 号码长度 BYTE
            int phone_length = phone_number.length ();

            // 3+n 联系人 STRING
            String contacts_name = user.getName ();

            // 2+n 联系人长度 BYTE
            int contacts_length = contacts_name.getBytes ().length;

            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                    BytesConvert.int2Bytes (call_type, 1), BytesConvert.int2Bytes (phone_length, 1),
                    phone_number.getBytes (), BytesConvert.int2Bytes (contacts_length, 1), contacts_name.getBytes ()));
        }
        message.setBodyLength (message.getMessageBody ().length);
    }

    public void downloadDriveParam (Map <String, Object> cmdMap, T808Message message) {

        String setParam = String.valueOf (cmdMap.get ("command"));
        String icontent = (String) cmdMap.get ("content");

        if (setParam.equalsIgnoreCase ("82")) {// 设置车辆信息

            String c [] = icontent.split (",");
            if (c.length >= 3) {
                // 车辆识别代号
                String car_code = c [0];
                // 车牌号
                String car_no = c [1];
                // 车牌分类
                String car_type = c [2];

                message.setBodyLength (message.getBodyLength () + 41);
                message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (), car_code.getBytes (),
                        car_no.getBytes (), car_type.getBytes ()));

            }

        }
        else if (setParam.equalsIgnoreCase ("83")) {// 设置初次安装日期
            message.setBodyLength (message.getBodyLength () + 6);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (), BytesConvert.str2Bcd (icontent)));

        }
        else if (setParam.equalsIgnoreCase ("84")) {// 设置状态量配置信息
            String c [] = icontent.split (",");
            if (c.length >= 8) {
                String d0 = c [0];
                String d1 = c [1];
                String d2 = c [2];
                String d3 = c [3];
                String d4 = c [4];
                String d5 = c [5];
                String d6 = c [6];
                String d7 = c [7];

                message.setBodyLength (message.getBodyLength () + 80);
                message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (), d0.getBytes (),
                        d1.getBytes (), d2.getBytes (), d3.getBytes (), d4.getBytes (), d5.getBytes (), d6.getBytes (),
                        d7.getBytes ()));
            }
        }
        else if (setParam.equalsIgnoreCase ("c2")) {// 设置记录仪时间
            message.setBodyLength (message.getBodyLength () + 6);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (), BytesConvert.str2Bcd (icontent)));

        }
        else if (setParam.equalsIgnoreCase ("c3")) {// 设置记录仪脉冲系数

            String c [] = icontent.split (",");
            if (c.length >= 3) {
                String nowTime = c [0];
                String highByte = c [1];
                String lowByte = c [2];

                message.setBodyLength (message.getBodyLength () + 8);
                message.setMessageBody (BytesConvert.concatAll (BytesConvert.str2Bcd (nowTime), highByte.getBytes (),
                        lowByte.getBytes ()));

            }
        }
        else if (setParam.equalsIgnoreCase ("c4")) {// 设置起始里程
            message.setBodyLength (message.getBodyLength () + 4);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (), BytesConvert.str2Bcd (icontent)));
        }

    }

    public void setFence (int fence_type, int oper_type, List <FenceVo> fences, T808Message message) {

        if (fence_type == FenceVo.TYPE_CIRCLE) {// 圆形

            message.setMessageBody (new byte [] {
                    (byte) oper_type, (byte) fences.size ()
            });
            for (FenceVo fence : fences) {

                int aree_id = fence.getId ();
                int prop = fence.getProperty ();

                Double [] center = fence.getCenter ();
                Double lat = null;
                Double lng = null;
                if (center != null && center.length >= 2) {
                    lng = center [0];
                    lat = center [1];
                }

                // 半径
                int radius = fence.getRadius ();

                message.setBodyLength (message.getBodyLength () + 18);
                message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                        BytesConvert.int2Bytes (aree_id, 4), BytesConvert.int2Bytes (prop, 2),
                        BytesConvert.int2Bytes ((int) (lat * 1000000), 4),
                        BytesConvert.int2Bytes ((int) (lng * 1000000), 4), BytesConvert.int2Bytes (radius, 4)));

                if ((prop & 0x01) == 1) {
                    String begin_time = fence.getBeginTime ();
                    String end_time = fence.getEndTime ();

                    message.setBodyLength (message.getBodyLength () + 12);
                    message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                            BytesConvert.str2Bcd (begin_time), BytesConvert.str2Bcd (end_time)));
                }

                if ((prop & 0x02) == 1) {
                    int maxSpeed = fence.getMaxSpeed ();
                    int overTime = fence.getOverSpeedTime ();

                    message.setBodyLength (message.getBodyLength () + 3);
                    message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                            BytesConvert.int2Bytes (maxSpeed, 2), BytesConvert.int2Bytes (overTime, 1)));
                }
            }

        }
        else if (fence_type == FenceVo.TYPE_RECTANGLE) {// 矩形

            message.setBodyLength (message.getBodyLength () + 2);

            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                    BytesConvert.int2Bytes (oper_type, 1), BytesConvert.int2Bytes (fences.size (), 1)));

            for (FenceVo fence : fences) {

                int aree_id = fence.getId ();
                int prop = fence.getProperty ();

                message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                        BytesConvert.int2Bytes (aree_id, 4), BytesConvert.int2Bytes (prop, 2)));

                SpotVo [] region = fence.getRegion ();
                for (SpotVo spot : region) {
                    message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                            BytesConvert.int2Bytes ((int) (spot.getLat () * 1000000), 4),
                            BytesConvert.int2Bytes ((int) (spot.getLng () * 1000000), 4)));
                }
                message.setBodyLength (message.getBodyLength () + 22);

                if ((prop & 0x01) == 1) {
                    String begin_time = fence.getBeginTime ();
                    String end_time = fence.getEndTime ();

                    message.setBodyLength (message.getBodyLength () + 12);
                    message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                            BytesConvert.str2Bcd (begin_time), BytesConvert.str2Bcd (end_time)));
                }

                if ((prop & 0x02) == 1) {
                    int maxSpeed = fence.getMaxSpeed ();
                    int overTime = fence.getOverSpeedTime ();

                    message.setBodyLength (message.getBodyLength () + 3);
                    message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                            BytesConvert.int2Bytes (maxSpeed, 2), BytesConvert.int2Bytes (overTime, 1)));
                }
            }

        }
        else if (fence_type == FenceVo.TYPE_POLYGON) {// 多边形

            for (FenceVo fence : fences) {

                int aree_id = fence.getId ();
                int prop = fence.getProperty ();

                message.setBodyLength (message.getBodyLength () + 6);
                message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                        BytesConvert.int2Bytes (aree_id, 4), BytesConvert.int2Bytes (prop, 2)));

                if ((prop & 0x01) == 1) {
                    String begin_time = fence.getBeginTime ();
                    String end_time = fence.getEndTime ();

                    message.setBodyLength (message.getBodyLength () + 12);
                    message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                            BytesConvert.str2Bcd (begin_time), BytesConvert.str2Bcd (end_time)));
                }

                if ((prop & 0x02) == 1) {
                    int maxSpeed = fence.getMaxSpeed ();
                    int overTime = fence.getOverSpeedTime ();

                    message.setBodyLength (message.getBodyLength () + 3);
                    message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                            BytesConvert.int2Bytes (maxSpeed, 2), BytesConvert.int2Bytes (overTime, 1)));
                }

                SpotVo [] region = fence.getRegion ();

                message.setBodyLength (message.getBodyLength () + 2);
                message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                        BytesConvert.int2Bytes (region.length, 2)));

                for (SpotVo spot : region) {
                    message.setBodyLength (message.getBodyLength () + 8);
                    message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                            BytesConvert.int2Bytes ((int) (spot.getLat () * 1000000), 4),
                            BytesConvert.int2Bytes ((int) (spot.getLng () * 1000000), 4)));
                }
            }
        }
        else if (fence_type == FenceVo.TYPE_LINE) {// 路线

            for (FenceVo fence : fences) {

                int aree_id = fence.getId ();
                int prop = fence.getProperty ();

                message.setBodyLength (message.getBodyLength () + 6);
                message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                        BytesConvert.int2Bytes (aree_id, 4), BytesConvert.int2Bytes (prop, 2)));

                if ((prop & 0x01) == 1) {
                    String begin_time = fence.getBeginTime ();
                    String end_time = fence.getEndTime ();

                    message.setBodyLength (message.getBodyLength () + 12);
                    message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                            BytesConvert.str2Bcd (begin_time), BytesConvert.str2Bcd (end_time)));
                }

                PointVo [] points = fence.getPoint ();

                message.setBodyLength (message.getBodyLength () + 2);
                message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                        BytesConvert.int2Bytes (points.length, 2)));

                for (PointVo point : points) {
                    // 拐点id
                    int point_id = point.getId ();
                    // 路段ID
                    int segment_id = point.getSegmentId ();
                    double lat = point.getLat ();
                    double lng = point.getLng ();
                    int width = point.getWidth ();
                    int segprop = point.getProp ();

                    message.setBodyLength (message.getBodyLength () + 18);
                    message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                            BytesConvert.int2Bytes (point_id, 4), BytesConvert.int2Bytes (segment_id, 4),
                            BytesConvert.int2Bytes ((int) (lat * 1000000), 4),
                            BytesConvert.int2Bytes ((int) (lng * 1000000), 4), BytesConvert.int2Bytes (width, 1),
                            BytesConvert.int2Bytes (segprop, 1)));

                    if ((segprop & 0x01) == 1) {
                        int min = point.getMin ();
                        int max = point.getMax ();

                        message.setBodyLength (message.getBodyLength () + 4);
                        message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                                BytesConvert.int2Bytes (max, 2), BytesConvert.int2Bytes (min, 2)));
                    }

                    if ((segprop & 0x02) == 1) {
                        // 路段最高速度
                        int maxSpeed = point.getMaxSpeed ();
                        // 路段超速持续时间
                        int overTime = point.getOverSpeedTime ();

                        message.setBodyLength (message.getBodyLength () + 3);
                        message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                                BytesConvert.int2Bytes (maxSpeed, 2), BytesConvert.int2Bytes (overTime, 1)));
                    }
                }
            }
        }

    }

    public void deviceCommand (T808Message msg, Map <String, Object> cmdMap) {

        int command = (Integer) cmdMap.get ("command");
        StringBuffer sbf = new StringBuffer ();
        Object apnName = cmdMap.get ("apnName");
        Object pppName = cmdMap.get ("pppName");
        Object userName = cmdMap.get ("userName");
        Object password = cmdMap.get ("password");
        Object ip = cmdMap.get ("ip");
        Object tcpPort = cmdMap.get ("tcpPort");
        Object udpPort = cmdMap.get ("udpPort");
        Object connectTime = cmdMap.get ("conectTime");
        switch (command) {
        case 1:
            Object url = cmdMap.get ("url");
            Object madeFrom = cmdMap.get ("madeFrom");
            Object hardware = cmdMap.get ("hardware");
            Object version = cmdMap.get ("version");
            sbf.append (Tools.toString (url))
                    .append (";")
                    .append (Tools.toString (apnName).equals ("") ? Tools.toString (pppName) : Tools.toString (apnName))
                    .append (";").append (Tools.toString (userName)).append (";").append (Tools.toString (password))
                    .append (";").append (Tools.toString (ip)).append (";").append (Tools.toString (tcpPort))
                    .append (";").append (Tools.toString (udpPort)).append (";").append (Tools.toString (madeFrom))
                    .append (";").append (Tools.toString (hardware)).append (";").append (Tools.toString (version))
                    .append (";").append (Tools.toString (connectTime));
            try {
                msg.setMessageBody (BytesConvert.concatAll (new byte [] {
                    (byte) command
                }, sbf.toString ().getBytes ("GBK")));
            }
            catch (UnsupportedEncodingException e) {
                log.error (" getbytes gbk error : {} , msg : {}", e.getMessage (), sbf.toString ());
            }
            break;
        case 2:
            Object connectType = cmdMap.get ("connectType");
            Object registId = cmdMap.get ("registId");
            sbf.append ((byte) Tools.toInt (connectType))
                    .append (";")
                    .append (Tools.toString (registId))
                    .append (";")
                    .append (Tools.toString (apnName).equals ("") ? Tools.toString (pppName) : Tools.toString (apnName))
                    .append (";").append (Tools.toString (userName)).append (";").append (Tools.toString (password))
                    .append (";").append (Tools.toString (ip)).append (";").append (Tools.toString (tcpPort))
                    .append (";").append (Tools.toString (udpPort)).append (";").append (Tools.toString (connectTime));
            try {
                msg.setMessageBody (BytesConvert.concatAll (new byte [] {
                    (byte) command
                }, sbf.toString ().getBytes ("GBK")));
            }
            catch (UnsupportedEncodingException e) {
                log.error (" getbytes gbk error : {} , msg : {}", e.getMessage (), sbf.toString ());
            }
            break;
        default:
            msg.setMessageBody (new byte [] {
                (byte) command
            });
            break;
        }
    }

    public void setSpeed (Map <String, Object> cmdMap, T808Message message, String deviceSn, int instructType) {

        Object maxObj = cmdMap.get ("max");
        Object minObj = cmdMap.get ("min");
        Object switchObj = cmdMap.get ("switch");
        if (Tools.checkEmpty (maxObj) || Tools.checkEmpty (minObj) || Tools.checkEmpty (switchObj))
            return;
        message.setMessageBody (BytesConvert.concatAll (new byte [] {
            2
        }, BytesConvert.int2Bytes (0x0055, 4), new byte [] {
            4
        }, BytesConvert.int2Bytes (Tools.toInt (switchObj) == 1 ? Tools.toInt (maxObj) : Integer.MAX_VALUE, 4),
                BytesConvert.int2Bytes (0x0056, 4), new byte [] {
                    4
                }, BytesConvert.int2Bytes (Tools.toInt (1), 4)

        ));
        this.instructCache
                .setSequenceNo (deviceSn, BytesConvert.bytes2Int (message.getMessageSequnce ()), instructType);
    }

    public void setMoveSwitch (Map <String, Object> cmdMap, T808Message message, String deviceSn, int instructType) {

        int moveSwitch = Tools.toInt (cmdMap.get ("switch"));
        if (moveSwitch == 1) {
            message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (1, 1),
                    BytesConvert.int2Bytes (0x54, 4), BytesConvert.int2Bytes (1, 1),
                    BytesConvert.int2Bytes (Tools.toInt (Math.pow (2, 28)), 4)));
        }
        else {
            message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (1, 1),
                    BytesConvert.int2Bytes (0x50, 4), BytesConvert.int2Bytes (4, 1),
                    BytesConvert.int2Bytes (Tools.toInt (Math.pow (2, 28)), 4)));
        }
        this.instructCache
                .setSequenceNo (deviceSn, BytesConvert.bytes2Int (message.getMessageSequnce ()), instructType);
    }

    public void sosAdd (Map <String, Object> cmdMap) {

        String sosNum = cmdMap.get ("numStr").toString ();
        List <Item> its = new ArrayList <Item> ();
        its.add (toItem (0x0040, sosNum));
        its.add (toItem (0x0043, sosNum));
        its.add (toItem (0x0044, sosNum));
        its.add (toItem (0x0048, sosNum));
        its.add (toItem (0x0049, sosNum));
        cmdMap.put ("all_params", its);

        List <PhoneItem> phones = new ArrayList <PhoneItem> ();
        int num = 1;
        phones.add (this.toPhoneItem (3, sosNum, "联系人" + num++));
        phones.add (this.toPhoneItem (3, sosNum, "联系人" + num++));
        phones.add (this.toPhoneItem (3, sosNum, "联系人" + num++));//呼入也可呼出
        cmdMap.put ("user_list", phones);
        cmdMap.put ("phone_type", 1);//此处写死，删除后追加
    }

    private static Item toItem (int itemId, String value) {

        Item it = new Item ();
        it.setId (itemId);
        it.setValue (value);
        return it;
    }

    private PhoneItem toPhoneItem (int type, String phoneNo, String contextsName) {

        PhoneItem itm = new PhoneItem ();
        itm.setId (type);
        itm.setPhone (phoneNo);
        itm.setName (contextsName);
        return itm;
    }

    public int setFence (Map <String, Object> cmdMap, T808Message message, String sn, int instructType) {

        int fence_type = Tools.toInt (cmdMap.get ("type"));
        int condition = Tools.toInt (cmdMap.get ("condition"));//1进，2出
        int messageId = 0;
        if (fence_type == FenceVo.TYPE_CIRCLE) {
            messageId = 0x8600;
            Double [] center = (Double []) cmdMap.get ("center");
            int radius = Tools.toInt (cmdMap.get ("radius"));
            message.setMessageBody (BytesConvert.concatAll (new byte [] {
                    0, 1
            }, BytesConvert.int2Bytes (1, 4), BytesConvert.int2Bytes (condition == 1 ? 8 : 32, 2),
                    BytesConvert.double2Bytes (center [0], 4), BytesConvert.double2Bytes (center [1], 4),
                    BytesConvert.int2Bytes (radius, 4)));
        }
        else if (fence_type == FenceVo.TYPE_RECTANGLE) {
            messageId = 0x8602;
            try {
                List <LinkedHashMap <String, Object>> region = JsonUtils.getList (cmdMap.get ("region").toString ());
                message.setMessageBody (BytesConvert.concatAll (new byte [] {
                        0, 1
                }, BytesConvert.int2Bytes (1, 4), BytesConvert.int2Bytes (condition == 1 ? 8 : 32, 2), BytesConvert
                        .int2Bytes ((int) ((Tools.toDouble (region.get (3).get ("lng")) * (Math.pow (10, 6)))), 4),
                        BytesConvert.int2Bytes (
                                (int) ((Tools.toDouble (region.get (3).get ("lat")) * (Math.pow (10, 6)))), 4),
                        BytesConvert.int2Bytes (
                                (int) ((Tools.toDouble (region.get (1).get ("lng")) * (Math.pow (10, 6)))), 4),
                        BytesConvert.int2Bytes (
                                (int) ((Tools.toDouble (region.get (1).get ("lat")) * (Math.pow (10, 6)))), 4)));
            }
            catch (Exception e) {
                log.error ("", e);
            }

        }
        else if (fence_type == FenceVo.TYPE_POLYGON) {
            messageId = 0x8604;
        }
        else if (fence_type == FenceVo.TYPE_LINE) {
            messageId = 0x8606;
        }
        this.instructCache.setSequenceNo (sn, BytesConvert.bytes2Int (message.getMessageSequnce ()), instructType);
        return messageId;
    }

    public int delFence (Map <String, Object> cmdMap, T808Message message, String sn, int instructType) {

        int fence_type = Tools.toInt (cmdMap.get ("type"));
        int messageId = 0;
        if (fence_type == FenceVo.TYPE_CIRCLE) {
            messageId = 0x8601;
        }
        else if (fence_type == FenceVo.TYPE_RECTANGLE) {
            messageId = 0x8603;
        }
        else if (fence_type == FenceVo.TYPE_POLYGON) {
            messageId = 0x8605;
        }
        else if (fence_type == FenceVo.TYPE_LINE) {
            messageId = 0x8607;
        }
        byte [] bytes = sn.getBytes ();
        message.setMessageBody (BytesConvert.concatAll (new byte [] {
            1
        }, bytes));
        this.instructCache.setSequenceNo (sn, BytesConvert.bytes2Int (message.getMessageSequnce ()), instructType);
        return messageId;

    }

    public List <T808Message> fenceSwitch (Map <String, Object> cmdMap, T808Message message, String sn,
            int instructType, IoSession session, int fenceSwitch) {

        T808Message fenceMsg = new T808Message ();
        List <T808Message> ls = new ArrayList <T808Message> ();
        ls.add (fenceMsg);
        this.toMsgId (fenceMsg, 0x8103).toMsgSecquent (fenceMsg, session);
        //fenceSwitch 1 开围栏 2 关围栏
        if (fenceSwitch == 1) {
            //                messageId = this.encodeUtls.setFence (cmdMap, message, deviceSn, instructType.getNum ());
            message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (1, 1),
                    BytesConvert.int2Bytes (0x54, 4), BytesConvert.int2Bytes (1, 1),
                    BytesConvert.int2Bytes (Tools.toInt (Math.pow (2, 20)), 4)));
            fenceMsg.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (1, 1),
                    BytesConvert.int2Bytes (0x54, 4), BytesConvert.int2Bytes (1, 1),
                    BytesConvert.int2Bytes (Tools.toInt (Math.pow (2, 21)), 4)));
        }
        else if (fenceSwitch == 2) {
            //                messageId = this.encodeUtls.delFence (cmdMap, message, deviceSn, instructType.getNum ());
            message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (1, 1),
                    BytesConvert.int2Bytes (0x50, 4), BytesConvert.int2Bytes (4, 1),
                    BytesConvert.int2Bytes (Tools.toInt (Math.pow (2, 20)), 4)));
            fenceMsg.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (1, 1),
                    BytesConvert.int2Bytes (0x50, 4), BytesConvert.int2Bytes (1, 1),
                    BytesConvert.int2Bytes (Tools.toInt (Math.pow (2, 21)), 4)));
        }
        this.instructCache.setSequenceNo (sn, BytesConvert.bytes2Int (message.getMessageSequnce ()), instructType);
        return ls;
    }

}
