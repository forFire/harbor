package com.capcare.harbor.handler.device.t808;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import module.util.BytesConvert;
import module.util.DateUtil;
import module.util.Tools;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.DeviceParam;
import com.capcare.harbor.model.DeviceVehicle;
import com.capcare.harbor.model.DriverInfo;
import com.capcare.harbor.model.ItemReply;
import com.capcare.harbor.model.MediaEvent;
import com.capcare.harbor.model.Position;
import com.capcare.harbor.model.PositionExtra;
import com.capcare.harbor.model.StatusSignal;
import com.capcare.harbor.protocol.Alarm;
import com.capcare.harbor.service.cache.InstructCache;
import com.capcare.harbor.service.logic.AlarmService;
import com.capcare.harbor.service.logic.bean.DeviceRegistRsBean;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.DeviceReply;
import com.capcare.harbor.vo.InstructType;

@SuppressWarnings("unused")
@Component
@Scope("singleton")
public class T808CodeUtils {

    private static final String messageNo     = "msgNo";
    private static int          UPGREADE_SIZE = 1024;

    private Logger              logger        = LoggerFactory.getLogger (getClass ());

    @Resource
    private AlarmService        alarmService;

    @Resource
    private InstructCache       instructCache;

    public T808CodeUtils toMsgSecquent (T808Response response, IoSession session) {

        response.setMessageSequnce (BytesConvert.int2Bytes (this.addMsgNo (session), 2));
        return this;
    }

    public T808CodeUtils toMsgId (T808Response response, int msgId) {

        return toMsgId (response, BytesConvert.int2Bytes (msgId, 2));
    }

    public T808CodeUtils toMsgId (T808Response response, byte [] data) {

        response.setMessageId (data);
        return this;
    }

    public List <String> resolveMessage (IoBuffer in) {

        in.mark ();
        int limit = in.limit ();
        byte [] bytes = new byte [limit];
        try {
            in.get (bytes);
        }
        catch (BufferUnderflowException e) {

            in.reset ();
            return null;
        }
        //        in.reset ();

        String hexs = BytesConvert.encodeHexStr (bytes);

        logger.info (" t808 convert message begin ：[" + hexs + "]");

        String [] split = hexs.split ("7E7E");
        if (split != null) {
            List <String> ls = new ArrayList <String> ();
            if (split.length == 1) {
                ls.add (this.getMessage (split [0], true, true));
            }
            else {
                for (int num = 0; num < split.length; num++) {
                    if (num == 0)
                        ls.add (this.getMessage (split [num], true, false));
                    else if (num == split.length - 1)
                        ls.add (this.getMessage (split [num], false, true));
                    else
                        ls.add (this.getMessage (split [num], false, false));
                }
            }
            return ls;
        }
        return null;
    }

    private String getMessage (String hexs, boolean hasHead, boolean hasEnd) {

        // 消息的第一个标志
        String head = hasHead ? hexs.substring (0, 2) : "7E";
        // 消息的最后一个标志
        String tail = hasEnd ? hexs.substring (hexs.length () - 2, hexs.length ()) : "7E";

        if (!head.equals ("7E") || !tail.equals ("7E")) {
            return null;
        }

        // 去除标志位的消息
        String message = hexs.substring (hasHead ? 2 : 0, hasEnd ? (hexs.length () - 2) : hexs.length ());

        // 转义还原
        message = message.replaceAll ("7D02", "7E");
        message = message.replaceAll ("7D01", "7D");

        logger.info (" t808 convert message end ：[" + message + "]");

        return message;
    }

    public boolean checkCode (String message, String requestCode) {

        byte [] bytes = BytesConvert.hexStringToBytes (message);

        // 计算得到的校验码
        int check_code = 0;
        for (byte b : bytes) {
            check_code ^= b;
        }

        // 消息中的校验码
        byte [] req_code = BytesConvert.hexStringToBytes (requestCode);

        int result = check_code ^ req_code [0];

        if (result == 0) {
            return true;
        }

        return false;
    }

    private Object getDeviceParam (int param_id, int skip, int param_length, byte [] data) {

        Object ret = null;

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
        case 0x0083:
            ret = BytesConvert.bytes2Int (new byte [] {
                    data [skip + 0], data [skip + 1], data [skip + 2], data [skip + 3]
            });
            break;
        case 0x0010:
        case 0x0011:
        case 0x0012:
        case 0x0013:
        case 0x0014:
        case 0x0015:
        case 0x0016:
        case 0x0017:
        case 0x0040:
        case 0x0041:
        case 0x0042:
        case 0x0043:
        case 0x0044:
        case 0x0048:
        case 0x0049:

            try {
                ret = new String (data, skip, param_length, "GBK");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace ();
            }

            break;
        case 0x0081:
        case 0x0082:
            ret = BytesConvert.bytes2Int (new byte [] {
                    data [skip + 0], data [skip + 1]
            });
            break;
        case 0x0084:
            ret = BytesConvert.bytes2Int (new byte [] {
                data [skip + 0]
            });
            break;
        default:
            break;
        }
        return ret;
    }

    private Object getExtraContent (int param_id, byte [] data) {

        Object ret = null;
        switch (param_id) {
        case 0x01:
            ret = BytesConvert.bytes2Int (new byte [] {
                    data [0], data [1], data [2], data [3]
            });
            break;
        case 0x02:
            ret = BytesConvert.bytes2Int (new byte [] {
                    data [0], data [1]
            });
            break;
        case 0x03:
            ret = BytesConvert.bytes2Int (new byte [] {
                    data [0], data [1]
            });
            break;
        case 0x04:
            ret = BytesConvert.bytes2Int (new byte [] {
                    data [0], data [1]
            });
            break;
        case 0x11:
            // 位置类型
            int ptype_speed = BytesConvert.bytes2Int (new byte [] {
                data [0]
            });
            if (ptype_speed > 0) {
                // 区域或线路ID
                int parea = BytesConvert.bytes2Int (new byte [] {
                        data [1], data [2], data [3], data [4]
                });
            }
            break;
        case 0x12:
            // 位置类型
            int ptype = BytesConvert.bytes2Int (new byte [] {
                data [0]
            });
            // 区域或线路ID
            int parea = BytesConvert.bytes2Int (new byte [] {
                    data [1], data [2], data [3], data [4]
            });
            // 方向
            int pdirection = BytesConvert.bytes2Int (new byte [] {
                data [5]
            });
            String str = ptype + "," + pdirection;
            ret = str;
            break;
        case 0x13:
            // 路段ID DWORD
            int route_id = BytesConvert.bytes2Int (new byte [] {
                    data [0], data [1], data [2], data [3]
            });
            // 路段行驶时间 WORD
            int route_time = BytesConvert.bytes2Int (new byte [] {
                    data [4], data [5]
            });
            // 结果 WORD
            int result = BytesConvert.bytes2Int (new byte [] {
                data [6]
            });
            break;
        default:
            break;
        }
        return ret;
    }

    public DeviceParam getDeviceParam (byte [] databody) {

        DeviceParam param = new DeviceParam ();

        // 应答流水号(对应的终端参数查询消息的流水号)
        int answer_number = BytesConvert.bytes2Int (new byte [] {
                databody [0], databody [1]
        });
        // 包参数个数
        int package_num = BytesConvert.bytes2Int (new byte [] {
            databody [2]
        });

        param.setSerial (answer_number);
        param.setNum (package_num);

        List <ItemReply> list = new ArrayList <ItemReply> ();
        param.setItems (list);

        int len = 3;
        for (int i = 0; i < package_num; i++) {

            ItemReply ir = new ItemReply ();

            // 参数id
            int param_id = BytesConvert.bytes2Int (new byte [] {
                    databody [0 + len], databody [1 + len], databody [2 + len], databody [3 + len]
            });
            // 参数长度
            int param_length = BytesConvert.bytes2Int (new byte [] {
                databody [4 + len]
            });
            // 参数值
            Object param_value = getDeviceParam (param_id, 5 + len, param_length, databody);
            len += param_length + 5;

            ir.setId (param_id);
            ir.setLength (param_length);
            ir.setValue (param_value.toString ());
            list.add (ir);
        }

        return param;
    }

    public MediaEvent getMediaEventMessage (byte [] databody) {

        MediaEvent media = new MediaEvent ();

        int mediaId = BytesConvert.bytes2Int (new byte [] {
                databody [0], databody [1], databody [2], databody [3]
        });
        int mediaType = BytesConvert.bytes2Int (new byte [] {
            databody [4]
        });
        int mediaCode = BytesConvert.bytes2Int (new byte [] {
            databody [5]
        });
        int eventCode = BytesConvert.bytes2Int (new byte [] {
            databody [6]
        });
        int channelId = BytesConvert.bytes2Int (new byte [] {
            databody [7]
        });

        media.setMediaId (mediaId);
        media.setChannelId (channelId);
        media.setMediaType (mediaType);
        media.setMediaCode (mediaCode);
        media.setEventCode (eventCode);
        return media;
    }

    public DriverInfo getDriverInfo (byte [] databody) {

        DriverInfo driver = new DriverInfo ();

        try {
            // 驾驶员姓名长度
            int nameLength = BytesConvert.bytes2Int (new byte [] {
                databody [0]
            });
            // 驾驶员姓名
            String driverName = new String (databody, 1, nameLength, "GBK");
            // 驾驶员身份证
            String driverNo = new String (databody, 1 + nameLength, 20, "GBK");
            // 从业资格证编码
            String cardCode = new String (databody, 21 + nameLength, 40, "GBK");
            // 发证机构名称长度
            int certLength = BytesConvert.bytes2Int (new byte [] {
                databody [61 + nameLength]
            });
            // 发证机构名称
            String certName = new String (databody, 62 + nameLength, certLength, "GBK");

            driver.setNameLength (nameLength);
            driver.setDriverName (driverName);
            driver.setDriverNo (driverNo);
            driver.setCardCode (cardCode);
            driver.setCertLength (certLength);
            driver.setCertName (certName);

            return driver;

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace ();
        }

        return null;

    }

    public DeviceVehicle registerDevice (byte [] databody, String mobile) {

        DeviceVehicle dv = new DeviceVehicle ();

        dv.setProvinceId (BytesConvert.bytes2Int (new byte [] {
                databody [0], databody [1]
        }));
        dv.setCityId (BytesConvert.bytes2Int (new byte [] {
                databody [2], databody [3]
        }));

        try {

            String manufacturerId = new String (databody, 4, 5, "GBK");
            dv.setManufacturerId (manufacturerId);

            if (databody.length == 25) {
                String model = new String (databody, 9, 8, "GBK");
                dv.setDeviceModel (model);
                byte [] deviceId = new byte [7];
                System.arraycopy (databody, 17, deviceId, 0, deviceId.length);
                String deviceID = new String (BytesConvert.bytes2Int (deviceId) + "");
                dv.setDeviceId (mobile);

                dv.setColor (BytesConvert.bytes2Int (new byte [] {
                    databody [24]
                }));

                // 车牌
                //        byte [] pinfo = new byte [databody.length - 37];
                //        System.arraycopy (databody, 37, pinfo, 0, pinfo.length);
                String plate = "";
                plate = new String (databody, 25, (databody.length - 24 - 1), "GBK");
                dv.setPlate (plate);

            }
            else {
                String model = new String (databody, 9, 20, "GBK");
                dv.setDeviceModel (model);
                byte [] deviceId = new byte [7];
                String deviceID = new String (databody, 29, 7, "GBK");
                dv.setDeviceId (deviceID);

                dv.setColor (BytesConvert.bytes2Int (new byte [] {
                    databody [36]
                }));

                // 车牌
                //        byte [] pinfo = new byte [databody.length - 37];
                //        System.arraycopy (databody, 37, pinfo, 0, pinfo.length);
                String plate = "";
                plate = new String (databody, 37, (databody.length - 36 - 1), "GBK");
                dv.setPlate (plate);
            }
        }
        catch (UnsupportedEncodingException e1) {
            logger.error ("", e1);
        }
        return dv;
    }

    public Position getPositionBaseMessage (byte [] databody) {

        Position position = new Position ();
        // 位置基本信息
        // 报警标志
        int alarmType = BytesConvert.bytes2Int (new byte [] {
                databody [0], databody [1], databody [2], databody [3]
        });
        int status = BytesConvert.bytes2Int (new byte [] {
                databody [4], databody [5], databody [6], databody [7]
        });
        int lat = BytesConvert.bytes2Int (new byte [] {
                databody [8], databody [9], databody [10], databody [11]
        });
        int lng = BytesConvert.bytes2Int (new byte [] {
                databody [12], databody [13], databody [14], databody [15]
        });

        int height = BytesConvert.bytes2Int (new byte [] {
                databody [16], databody [17]
        });
        int speed = BytesConvert.bytes2Int (new byte [] {
                databody [18], databody [19]
        });
        int direction = BytesConvert.bytes2Int (new byte [] {
                databody [20], databody [21]
        });
        String time = BytesConvert.encodeHexStr (new byte [] {
                databody [22], databody [23], databody [24], databody [25], databody [26], databody [27]
        });

        position.setAlarmType (alarmType);
        position.setStatus (status);
        position.setLat (lat);
        position.setLng (lng);
        position.setHeight (height);
        position.setSpeed (speed);
        position.setDirection (direction);
        position.setTime (time);

        return position;

    }

    public Position getPositionMessage (byte [] databody) {

        Position position = new Position ();
        // 位置基本信息
        // 报警标志
        int alarmType = BytesConvert.bytes2Int (new byte [] {
                databody [0], databody [1], databody [2], databody [3]
        });
        int status = BytesConvert.bytes2Int (new byte [] {
                databody [4], databody [5], databody [6], databody [7]
        });
        int lat = BytesConvert.bytes2Int (new byte [] {
                databody [8], databody [9], databody [10], databody [11]
        });
        int lng = BytesConvert.bytes2Int (new byte [] {
                databody [12], databody [13], databody [14], databody [15]
        });

        int height = BytesConvert.bytes2Int (new byte [] {
                databody [16], databody [17]
        });
        int speed = BytesConvert.bytes2Int (new byte [] {
                databody [18], databody [19]
        });
        int direction = BytesConvert.bytes2Int (new byte [] {
                databody [20], databody [21]
        });
        String time = BytesConvert.encodeHexStr (new byte [] {
                databody [22], databody [23], databody [24], databody [25], databody [26], databody [27]
        });

        position.setAlarmType (alarmType);
        position.setStatus (status);
        position.setLat (lat);
        position.setLng (lng);
        position.setHeight (height);
        position.setSpeed (speed);
        position.setDirection (direction);
        position.setTime (time);

        HashMap <String, PositionExtra> map = new HashMap <String, PositionExtra> ();
        // 附加项长度
        int extra_length = databody.length - 28;

        int skip = 0;
        while (extra_length > 0) {

            // 位置附加信息项
            int extra_id = BytesConvert.bytes2Int (new byte [] {
                databody [28 + skip]
            });
            int extra_len = BytesConvert.bytes2Int (new byte [] {
                databody [29 + skip]
            });

            byte [] subdata = new byte [databody.length - skip - 30];
            System.arraycopy (databody, skip + 30, subdata, 0, subdata.length);

            Object extra_content = getExtraContent (extra_id, subdata);
            skip = skip + extra_len + 2;
            extra_length = extra_length - extra_len - 2;
            if (extra_content == null)
                continue;

            if (extra_id == 12)
                logger.info ("fence alarm : {}", extra_content);

            PositionExtra extra = new PositionExtra ();
            extra.setId (extra_id);
            extra.setLength (extra_len);
            extra.setValue (extra_content.toString ());
            map.put (String.valueOf (extra_id), extra);
        }
        position.setExtra (map);
        return position;
    }

    public void getDataRecorder (byte [] databody) throws UnsupportedEncodingException {

        // 命令字
        int command = BytesConvert.bytes2Int (new byte [] {
            databody [2]
        });

        switch (command) {
        case 0:
            int yearNo = BytesConvert.bytes2Int (new byte [] {
                databody [3]
            });
            int order = BytesConvert.bytes2Int (new byte [] {
                databody [4]
            });
            break;
        case 1:
            String driverNo = new String (databody, 3, 18, "GBK");
            break;
        case 2:
            // int year = BytesConvert.bytes2Int(new byte[] { databody[3] });
            // int month = BytesConvert.bytes2Int(new byte[] { databody[4] });
            // int day = BytesConvert.bytes2Int(new byte[] { databody[5] });
            // int hour = BytesConvert.bytes2Int(new byte[] { databody[6] });
            // int minute = BytesConvert.bytes2Int(new byte[] { databody[7] });
            // int second = BytesConvert.bytes2Int(new byte[] { databody[8] });

            String rTime = BytesConvert.encodeHexStr (databody [3], databody [4], databody [5], databody [6],
                    databody [7], databody [8]);
        case 3:
            // 实时时间
            String realTime = BytesConvert.encodeHexStr (databody [3], databody [4], databody [5], databody [6],
                    databody [7], databody [8]);
            // 初次安装时间
            String setupTime = BytesConvert.encodeHexStr (databody [9], databody [10], databody [11], databody [12],
                    databody [13], databody [14]);

            // // 初次安装时间
            // int setup_year = BytesConvert.bytes2Int(new byte[] { databody[9]
            // });
            // int setup_month = BytesConvert
            // .bytes2Int(new byte[] { databody[10] });
            // int setup_day = BytesConvert.bytes2Int(new byte[] { databody[11]
            // });
            // int setup_hour = BytesConvert
            // .bytes2Int(new byte[] { databody[12] });
            // int setup_minute = BytesConvert
            // .bytes2Int(new byte[] { databody[13] });
            // int setup_second = BytesConvert
            // .bytes2Int(new byte[] { databody[14] });

            // 初始里程
            String initMileage = BytesConvert.encodeHexStr (databody [15], databody [16], databody [17], databody [18]);

            // 累计行驶里程
            String totalMileage = BytesConvert
                    .encodeHexStr (databody [19], databody [20], databody [21], databody [22]);
            break;
        case 4:
            // 记录仪当前时间
            String nowTime = BytesConvert.encodeHexStr (databody [3], databody [4], databody [5], databody [6],
                    databody [7], databody [8]);

            // 记录仪脉冲系数高字节
            int highByte = BytesConvert.bytes2Int (new byte [] {
                databody [9]
            });
            // 记录仪脉冲系数低字节
            int lowByte = BytesConvert.bytes2Int (new byte [] {
                databody [10]
            });
            break;
        case 5:
            // 车辆识别代号
            String vin = new String (databody, 3, 17, "GBK");
            // 机动车号牌号码
            String plateNum = new String (databody, 20, 12, "GBK");
            // 机动车号牌分类
            String vehicleType = new String (databody, 32, 12, "GBK");
            break;
        case 6:
            // 记录仪实时时间
            realTime = BytesConvert.encodeHexStr (databody [3], databody [4], databody [5], databody [6], databody [7],
                    databody [8]);
            // 状态信号字节个数 N
            int signalNum = BytesConvert.bytes2Int (new byte [] {
                databody [9]
            });
            // 状态信号配置
            int j6 = 0;
            ArrayList <StatusSignal> list6 = new ArrayList <StatusSignal> ();
            for (int i = 0; i < signalNum; i++) {
                StatusSignal ss = new StatusSignal ();
                ss.setD0 (new String (databody, 10 + j6, 10, "GBK"));
                ss.setD1 (new String (databody, 20 + j6, 10, "GBK"));
                ss.setD2 (new String (databody, 30 + j6, 10, "GBK"));
                ss.setD3 (new String (databody, 40 + j6, 10, "GBK"));
                ss.setD4 (new String (databody, 50 + j6, 10, "GBK"));
                ss.setD5 (new String (databody, 60 + j6, 10, "GBK"));
                ss.setD6 (new String (databody, 70 + j6, 10, "GBK"));
                ss.setD7 (new String (databody, 80 + j6, 10, "GBK"));
                list6.add (ss);
                j6 += 80;
            }
            break;
        case 7:
            // 生产厂 CCC 认证代码
            String attestationCode = BytesConvert.encodeHexStr (databody [3], databody [4], databody [5], databody [6],
                    databody [7], databody [8], databody [9]);
            // 认证产品型号
            String productModel = new String (databody, 10, 16, "GBK");
            // 记录仪的生产日期
            String pTime = BytesConvert.encodeHexStr (databody [26], databody [27], databody [28]);
            // 产品生产流水号
            String serialNumber = BytesConvert
                    .encodeHexStr (databody [29], databody [30], databody [31], databody [32]);
            // 备用
            String reserve = BytesConvert.encodeHexStr (databody [33], databody [34], databody [35], databody [36],
                    databody [37]);
            break;
        case 8:
            int summ = (int) Math.floor (databody.length / 126);// 总分钟数
            int j8 = 0;
            // ArrayList<String> list8 = new ArrayList<String>();
            for (int i = 0; i < summ; i++) {// 指定的行驶速度记录数据块
                String sTime = BytesConvert.encodeHexStr (databody [3 + j8], databody [4 + j8], databody [5 + j8],
                        databody [6 + j8], databody [7 + j8], databody [8 + j8]);
                for (int k = 0; k < 60; k++) {// 单位分钟行驶速度记录数据块
                    int g8 = 0;
                    int h8 = j8 + g8;
                    String avgSpeed = BytesConvert.encodeHexStr (databody [9 + h8]);
                    String signal = BytesConvert.encodeHexStr (databody [10 + h8]);
                    g8 += 2;
                }
                j8 += 126;
            }
            break;
        case 9:
            int sumh = (int) Math.floor (databody.length / 666);// 总小时数
            int j9 = 0;
            for (int i = 0; i < sumh; i++) {// 指定的位置信息记录数据块
                String sTime = BytesConvert.encodeHexStr (databody [3 + j9], databody [4 + j9], databody [5 + j9],
                        databody [6 + j9], databody [7 + j9], databody [8 + j9]);// 开始时间
                for (int k = 0; k < 60; k++) {// 单位小时位置信息数据块
                    int g9 = 0;
                    int h9 = j9 + g9;
                    String lng = BytesConvert.encodeHexStr (databody [9 + h9], databody [10 + h9], databody [11 + h9],
                            databody [12 + h9]);
                    String lat = BytesConvert.encodeHexStr (databody [13 + h9], databody [14 + h9], databody [15 + h9],
                            databody [16 + h9]);
                    String high = BytesConvert.encodeHexStr (databody [17 + h9], databody [18 + h9]);
                    String avgSpeed = BytesConvert.encodeHexStr (databody [19 + h9]);
                    g9 += 11;
                }
                j9 += 666;
            }

            break;
        case 10:
            int sume = (int) Math.floor (databody.length / 234);// 总事故数
            int j10 = 0;
            for (int i = 0; i < sume; i++) {
                // 行驶结束时间
                String eTime = BytesConvert.encodeHexStr (databody [3 + j10], databody [4 + j10], databody [5 + j10],
                        databody [6 + j10], databody [7 + j10], databody [8 + j10]);
                // 机动车驾驶证号码
                String license = new String (databody, 9 + j10, 18, "GBK");
                for (int j = 0; j < 100; j++) {
                    // 表示行驶结束时间之前 20s内，每 0.2s间隔采集 1 次，共 100组 0.2s的事故疑点记录，按时间倒序排列
                    int g10 = 0;
                    int h10 = j10 + g10;
                    // 行驶结束时的状态信号(第 1 字节)
                    String endStatus = BytesConvert.encodeHexStr (databody [27 + h10]);
                    // 行驶结束时的速度
                    String endSpeed = BytesConvert.encodeHexStr (databody [28 + h10]);

                    g10 += 2;

                }
                // 行驶结束时间前的最近一次有效位置信息
                String lng = BytesConvert.encodeHexStr (databody [227 + j10], databody [228 + j10],
                        databody [229 + j10], databody [230 + j10]);
                String lat = BytesConvert.encodeHexStr (databody [231 + j10], databody [232 + j10],
                        databody [233 + j10], databody [234 + j10]);
                String high = BytesConvert.encodeHexStr (databody [235 + j10], databody [236 + j10]);
                j10 += 234;
            }
            break;
        case 11:
            int sumc = (int) Math.floor (databody.length / 50);// 总超时驾驶数
            int j11 = 0;
            for (int i = 0; i < sumc; i++) {
                // 机动车驾驶证号码
                String license = new String (databody, 3 + j11, 18, "GBK");
                // 连续驾驶开始时间
                String stime = BytesConvert.encodeHexStr (databody [21 + j11], databody [22 + j11],
                        databody [23 + j11], databody [24 + j11], databody [25 + j11], databody [26 + j11]);
                // 连续驾驶结束时间
                String etime = BytesConvert.encodeHexStr (databody [27 + j11], databody [28 + j11],
                        databody [29 + j11], databody [30 + j11], databody [31 + j11], databody [32 + j11]);
                // 连续驾驶开始时间所在的最近一次有效位置信息
                String slng = BytesConvert.encodeHexStr (databody [33 + j11], databody [34 + j11], databody [35 + j11],
                        databody [36 + j11]);
                String slat = BytesConvert.encodeHexStr (databody [37 + j11], databody [38 + j11], databody [39 + j11],
                        databody [40 + j11]);
                String shigh = BytesConvert.encodeHexStr (databody [41 + j11], databody [42 + j11]);
                // 连续驾驶结束时间所在的最近一次有效位置信息
                String elng = BytesConvert.encodeHexStr (databody [43 + j11], databody [44 + j11], databody [45 + j11],
                        databody [46 + j11]);
                String elat = BytesConvert.encodeHexStr (databody [47 + j11], databody [48 + j11], databody [49 + j11],
                        databody [50 + j11]);
                String ehigh = BytesConvert.encodeHexStr (databody [51 + j11], databody [52 + j11]);

                j11 += 50;

            }
            break;
        case 12:
            int suml = (int) Math.floor (databody.length / 25);// 总登录退出记录数
            int j12 = 0;
            for (int i = 0; i < suml; i++) {
                // 事件发生时间
                String htime = BytesConvert.encodeHexStr (databody [3 + j12], databody [4 + j12], databody [5 + j12],
                        databody [6 + j12], databody [7 + j12], databody [8 + j12]);
                // 机动车驾驶证号码
                String license = new String (databody, 9 + j12, 18, "GBK");
                // 事件类型 (01H：登录，02H：退出，其他预留)
                String eType = BytesConvert.encodeHexStr (databody [27 + j12]);

                j12 += 25;
            }
            break;
        case 13:
            int sumd = (int) Math.floor (databody.length / 7);// 总外部电源记录数
            int j13 = 0;
            for (int i = 0; i < sumd; i++) {
                // 事件发生时间
                String htime = BytesConvert.encodeHexStr (databody [3 + j13], databody [4 + j13], databody [5 + j13],
                        databody [6 + j13], databody [7 + j13], databody [8 + j13]);
                // 事件类型( 01H 表示通电，02H 表示断电)
                String eType = BytesConvert.encodeHexStr (databody [9 + j13]);

                j13 += 7;
            }
            break;
        case 14:
            int sumx = (int) Math.floor (databody.length / 7);// 总参数修改记录数
            int j14 = 0;
            for (int i = 0; i < sumx; i++) {
                // 事件发生时间
                String htime = BytesConvert.encodeHexStr (databody [3 + j14], databody [4 + j14], databody [5 + j14],
                        databody [6 + j14], databody [7 + j14], databody [8 + j14]);
                // 事件类型
                String eType = BytesConvert.encodeHexStr (databody [9 + j14]);

                j14 += 7;
            }
            break;
        case 15:
            int sums = (int) Math.floor (databody.length / 133);// 总速度状态日志数
            int j15 = 0;
            for (int i = 0; i < sums; i++) {// 速度状态日志数据块格式
                // 记录仪的速度状态(01H 表示正常，02H表示异常)
                String status = BytesConvert.encodeHexStr (databody [3 + j15]);
                // 速度状态判定的开始时间
                String stime = BytesConvert.encodeHexStr (databody [4 + j15], databody [5 + j15], databody [6 + j15],
                        databody [7 + j15], databody [8 + j15], databody [9 + j15]);
                // 速度状态判定的结束时间
                String etime = BytesConvert.encodeHexStr (databody [10 + j15], databody [11 + j15],
                        databody [12 + j15], databody [13 + j15], databody [14 + j15], databody [15 + j15]);
                for (int j = 0; j < 60; j++) {// 单位速度状态日志数据块格式
                    int g15 = 0;
                    int h15 = j15 + g15;
                    // 开始时间后第 xx s对应的记录速度
                    String rSpeed = BytesConvert.encodeHexStr (databody [16 + h15]);
                    // 开始时间后第xx s对应的参考速度
                    String cSpeed = BytesConvert.encodeHexStr (databody [17 + h15]);

                    g15 += 2;
                }
            }
            break;
        }
    }

    private void toAlarmBean (String deviceSn, Position pos) throws Exception {

        Alarm alarm = new Alarm ();
        BitSet statusSet = BytesConvert.fromByte (BytesConvert.int2Bytes (pos.getStatus (), 2));
        //格式true/false
        /*boolean statusDetail = statusSet.get (0);//acc开关
        statusDetail = statusSet.get (1);//是否定位
        statusDetail = statusSet.get (2);//南纬北纬
        statusDetail = statusSet.get (3);//西经东经
        statusDetail = statusSet.get (4);//停运或运营
        statusDetail = statusSet.get (5);//经纬度加密或未加密
        statusDetail = statusSet.get (10);//油路断开或正常
        statusDetail = statusSet.get (11);//车辆电路断开或正常
        statusDetail = statusSet.get (12);//车门加锁或解锁
        */
        alarm.setAccMode (statusSet.get (0) ? 1 : 0);
        alarm.setDeviceSn (deviceSn);
        alarm.setLat (Tools.toDouble (pos.getLat ()) / Math.pow (10, 6));
        alarm.setLng (Tools.toDouble (pos.getLng ()) / Math.pow (10, 6));
        alarm.setMode (statusSet.get (1) ? "A" : "V");
        alarm.setSpeed (Tools.toDouble (pos.getSpeed ()));
        alarm.setDirection (Tools.toFloat (pos.getDirection ()));
        alarm.setRead (2);
        try {
            alarm.setTime (DateUtil.strLong (pos.getTime (), "yyMMddHHmmss"));
            alarm.setSystime (DateUtil.strLong (pos.getTime (), "yyMMddHHmmss"));
        }
        catch (ParseException e) {
            logger.info ("receive time error : {}  sn : {" + deviceSn + "}", pos.getTime ());
        }
        Integer alarmType = pos.getAlarmType ();
        byte [] int2Bytes = BytesConvert.int2Bytes (alarmType, 32);
        BitSet fromByte = BytesConvert.fromByte (int2Bytes);
        for (int num = 0; num < fromByte.size (); num++) {
            boolean arm = fromByte.get (num);
            if (arm)
                switch (num) {
                case 1:
                    alarm.setType (4);
                    alarm.setInfo ("超速报警");
                    alarmService.handleAlarm (alarm);
                    break;
                case 7:
                    alarm.setType (3);
                    alarm.setInfo ("低电报警");
                    alarmService.handleAlarm (alarm);
                    break;
                case 8:
                    alarm.setType (7);
                    alarm.setInfo ("拔出报警");
                    alarmService.handleAlarm (alarm);
                    break;
                case 20:
                    HashMap <String, PositionExtra> map = pos.getExtra ();
                    PositionExtra positionExtra = map.get ("12");
                    if (positionExtra == null)
                        positionExtra = map.get ("18");
                    String extra = positionExtra.getValue ();
                    if (extra == null)
                        break;
                    String arr [] = extra.split (",");
                    String type = arr [0];
                    String out = arr [1];
                    alarm.setType (out.equals ("0") ? 1 : 2);
                    alarm.setInfo (out.equals ("0") ? "进围栏报警" : "出围栏报警");
                    alarmService.handleAlarm (alarm);
                    break;
                case 28:
                    alarm.setType (6);
                    alarm.setInfo ("移动报警");
                    alarmService.handleAlarm (alarm);
                    break;
                default:
                    break;
                }
        }
    }

    public com.capcare.harbor.protocol.Position toPostitionBean (String deviceSn, Position pos) throws Exception {

        com.capcare.harbor.protocol.Position bean = new com.capcare.harbor.protocol.Position ();
        BitSet statusSet = BytesConvert.fromByte (BytesConvert.int2Bytes (pos.getStatus (), 2));
        //格式true/false
        /*boolean statusDetail = statusSet.get (0);//acc开关
        statusDetail = statusSet.get (1);//是否定位
        statusDetail = statusSet.get (2);//南纬北纬
        statusDetail = statusSet.get (3);//西经东经
        statusDetail = statusSet.get (4);//停运或运营
        statusDetail = statusSet.get (5);//经纬度加密或未加密
        statusDetail = statusSet.get (10);//油路断开或正常
        statusDetail = statusSet.get (11);//车辆电路断开或正常
        statusDetail = statusSet.get (12);//车门加锁或解锁
        */
        toAlarmBean (deviceSn, pos);

        bean.setLat (Tools.toDouble (pos.getLat ()) / 1000000);
        bean.setLng (Tools.toDouble (pos.getLng ()) / 1000000);
        bean.setSpeed (Tools.toDouble (pos.getSpeed ()));
        bean.setDirection (Tools.toFloat (pos.getDirection ()));
        bean.setAccMode (statusSet.get (0) ? 1 : 0);
        bean.setDeviceSn (deviceSn);
        bean.setMode (statusSet.get (1) ? "A" : "V");
        try {
            bean.setReceive (DateUtil.nowDate ().getTime ());
            bean.setSystime (DateUtil.strLong (pos.getTime (), "yyMMddHHmmss"));
        }
        catch (ParseException e) {
            logger.info ("receive time error : {}  sn : {}", pos.getTime ());
        }
        /*        List <PositionExtra> extra = pos.getExtra ();
                if (extra != null) {
                    for (PositionExtra ex : extra) {
                        Integer id = ex.getId ();
                        String value = ex.getValue ();
                    }
                }*/
        return bean;
    }

    public void defaultReply (IoSession session, T808Response response, byte [] data, byte rs) {

        int addMsgNo = this.addMsgNo (session);
        response.setMessageSequnce (BytesConvert.int2Bytes (addMsgNo, 2));//消息id
        response.setMessageId (BytesConvert.int2Bytes (0x8001, 2));//消息流水号
        response.setResultData (new byte [] {
                data [10], data [11], data [0], data [1], rs
        //终端流水号，终端消息id，结果
        });
    }

    public int addMsgNo (IoSession session) {

        int msgNo = (Integer) (session.getAttribute (messageNo) == null ? 0 : session.getAttribute (messageNo));
        synchronized (session) {
            session.setAttribute (messageNo, ++msgNo);
        }
        return msgNo;
    }

    public void registRs (DeviceRegistRsBean registBean, String sn, byte [] data, T808Response response,
            int message_number) {

        switch (registBean.getRegistRs ()) {
        case 0x0:
            byte [] regId;
            try {
                regId = registBean.getRegistId ().getBytes ("UTF-8");
                byte [] rs = new byte [3 + regId.length];
                byte [] int2Bytes = BytesConvert.int2Bytes (message_number, 2);
                rs [0] = int2Bytes [0];
                rs [1] = int2Bytes [1];
                rs [2] = (byte) registBean.getRegistRs ();
                for (int num = 3; num < rs.length; num++)
                    rs [num] = regId [num - 3];
                response.setResultData (rs);
            }
            catch (UnsupportedEncodingException e) {
                logger.error ("get registid error regid : {} sn : {} err : {}", new String [] {
                        registBean.getRegistId (), sn, e.getMessage ()
                });
            }
            break;
        default:
            response.setResultData (new byte [] {
                    data [0], data [1], (byte) registBean.getRegistRs ()
            });
            break;
        }

    }

    public static byte [] toRsBytes (byte [] ret) {

        int count7e = 0;
        for (int num = 0; num < ret.length - 1; num++) {//校验码
            ret [ret.length - 1] ^= ret [num];
            if (ret [num] == 0x7E || ret [num] == 0x7D)
                count7e++;
        }

        //转码量计算
        if (ret [ret.length - 1] == 0x7E || ret [ret.length - 1] == 0x7D)
            count7e++;

        byte [] rsbt = new byte [2 + count7e + ret.length];
        rsbt [0] = 0x7E;
        int num = 1;
        for (byte bt : ret) {//转码
            if (bt == 0x7E) {
                rsbt [num++] = 0x7D;
                rsbt [num++] = 0x02;
            }
            else if (bt == 0x7D) {
                rsbt [num++] = 0x7D;
                rsbt [num++] = 0x01;
            }
            else {
                rsbt [num++] = bt;
            }
        }
        rsbt [rsbt.length - 1] = 0x7E;
        return rsbt;
    }

    public BaseMessage defaultDeviceReply (byte [] databody, String deviceSn, T808Response response, IoSession session) {

        // 应答流水号(对应的平台消息的流水号)
        int answer_number = BytesConvert.bytes2Int (new byte [] {
                databody [0], databody [1]
        });
        // 应答ID(对应的平台消息的ID)
        int answer_id = BytesConvert.bytes2Int (new byte [] {
                databody [2], databody [3]
        });
        // 结果(0：成功/确认；1：失败；2：消息有误；3：不支持)
        int result = BytesConvert.bytes2Int (new byte [] {
            databody [4]
        });
        DeviceReply rep = new DeviceReply ();
        BaseMessage ret = new BaseMessage (Act.REPLY, rep);
        rep.setDeviceSn (deviceSn);
        rep.setSuccess (result == 0 ? true : false);
        if (result == 2 || result == 3)
            logger.error (" message is error : {} , sn : {}", result, deviceSn);
        switch (answer_id) {
        case 0x8108://接收升级分包、
            int pkgSubNo = instructCache.getPkgSubNo (deviceSn, answer_number);
            BaseMessage msg = this.upgradePkgSub (response, session, deviceSn, pkgSubNo);
            ret = msg;
            break;
        case 0x8103://参数设置      超速开关   速度设置
        case 0x8600://设置圆形围栏
        case 0x8602://设置方形围栏
        case 0x8604://设置多边形围栏
        case 0x8606://设置路线
        case 0x8601://删除圆形围栏
        case 0x8603://删除方形围栏
        case 0x8605://删除多边形围栏
        case 0x8607://删除路线
            int sequenceNo = this.instructCache.getSequenceNo (deviceSn, answer_number);
            if (sequenceNo != 0) {

                rep.setInstructType (InstructType.getByNum (sequenceNo));
                break;
            }
        default:
            rep.setInstructType (InstructType.getByNum (0));
            break;
        }
        return ret;

    }

    /** 
     *  @Description    : 目前只适合m2616
     *  @Method_Name    : upgrade
     *  @param databody
     *  @param response 
     *  @return         : void
     *  @Creation Date  : 2014年12月3日 下午4:20:02 
     *  @version        : v1.00
     * @return 
     *  @Author         : wangkun
     *  @Update Date    : 
     *  @Update Author  : wangkun
     */
    public BaseMessage upgrade (byte [] databody, T808Response response, IoSession session) {

        int deviceType = BytesConvert.bytes2Int (databody [0], databody [1]);
        String mfrsID = new String (databody, 2, 5);
        String deviceModel = new String (databody, 7, 4);
        String imei = new String (databody, 11, 14);
        String deviceSn = new String (databody, 25, 7);
        String ccid = new String (databody, 32, 10);
        String hardware = new String (databody, 42, 9);
        String software = new String (databody, 51, 1);
        String version = new String (databody, 52, 8);

        session.setAttribute ("upgradeVersion", version);
        session.setAttribute ("upgradeHardware", hardware);
        return getUpgradeMessage (response, hardware, version, deviceSn, 1);
    }

    private BaseMessage getUpgradeMessage (T808Response response, String hardware, String version, String deviceSn,
            int subNo) {

        BaseMessage ret = new BaseMessage (Act.UPGRADE_DOWNLOAD, null);
        T808UpgradeStream upgradeStream = T808UpgradeStream.getInstance (hardware, version);

        byte [] upgradeData = upgradeStream.getBytes (0, UPGREADE_SIZE);
        byte [] resultData = {};
        response.setResultData (resultData);
        if (upgradeStream.getPackageCount () > 1) {
            response.setSubpackage (true);
            response.setPackageCount (BytesConvert.int2Bytes (Tools.toInt (upgradeStream.getPackageCount ()), 2));
            response.setPackageSequence (BytesConvert.int2Bytes (1, 2));
            this.instructCache.setPkgSubNo (deviceSn, BytesConvert.bytes2Int (response.getMessageSequnce ()), subNo);
        }
        ret.setResponse (response);
        return ret;

    }

    private BaseMessage upgradeFinsh () {

        BaseMessage ret = new BaseMessage (Act.UPGRADE_FINISH, null);
        return ret;
    }

    public BaseMessage upgradePkgSub (T808Response response, IoSession session, String deviceSn, int subNo) {

        String version = Tools.toString (session.getAttribute ("upgradeVersion"));
        String hardware = Tools.toString (session.getAttribute ("upgradeHardware"));
        T808UpgradeStream upgradeInstance = T808UpgradeStream.getInstance (hardware, version);
        if (upgradeInstance.getPackageCount () == subNo) {
            return upgradeFinsh ();
        }
        else {
            BaseMessage upgradeMessage = this.getUpgradeMessage (response, hardware, version, deviceSn, subNo);
            return upgradeMessage;
        }
    }
}
