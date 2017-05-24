package com.capcare.harbor.handler.device.t808;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.annotation.Resource;

import module.util.BytesConvert;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.m2616.exception.NoIndexException;
import com.capcare.harbor.model.DeviceParam;
import com.capcare.harbor.model.DeviceVehicle;
import com.capcare.harbor.model.DriverInfo;
import com.capcare.harbor.model.MediaEvent;
import com.capcare.harbor.model.Position;
import com.capcare.harbor.service.logic.DeviceRegistService;
import com.capcare.harbor.service.logic.bean.DeviceRegistRsBean;
import com.capcare.harbor.service.logic.bean.MediaItem;
import com.capcare.harbor.service.logic.bean.MediaRetrievalReply;
import com.capcare.harbor.service.logic.bean.RSA;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.DeviceReply;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.LoginDevice;

@Component
@Scope("singleton")
@SuppressWarnings("unused")
public class T808Decoder {

    private static final Logger logger    = LoggerFactory.getLogger (T808Decoder.class);

    private static final String DEVICE_SN = "deviceSn";

    private static final String defaultSn = null;

    @Resource
    private DeviceRegistService registService;

    @Resource
    private T808CodeUtils       codeUtils;

    public List <BaseMessage> decode (IoSession session, IoBuffer in) {

        String deviceSn = session.getAttribute (DEVICE_SN) == null ? defaultSn : session.getAttribute (DEVICE_SN)
                .toString ();
        session.setAttribute (DEVICE_SN, deviceSn);
        // 校验数据包
        boolean check = checkInBytes (in);
        if (!check) {
            return null;
        }

        // 转义还原——>验证校验码——>解析消息
        List <String> messagels = codeUtils.resolveMessage (in);
        if (messagels == null)
            return null;
        List <BaseMessage> ls = new ArrayList <BaseMessage> ();
        for (String message : messagels) {
            BaseMessage baseMsg = this.getMessage (session, in, message, deviceSn);
            if (baseMsg != null) {
                if (deviceSn == null)
                    session.setAttribute (DEVICE_SN, baseMsg.getLoginDevice ().getSn ());
                ls.add (baseMsg);
            }
        }
        return ls;
    }

    private BaseMessage getMessage (IoSession session, IoBuffer in, String message, String deviceSn) {

        //        in.position (1);
        System.out.println ("=====" + in.position () + "=====" + in.hasRemaining ());
        if (message == null) {
            return null;
        }

        // 校验码
        String check_code = message.substring (message.length () - 2, message.length ());
        // 消息头+消息体
        message = message.substring (0, message.length () - 2);

        // 校验码比较
        boolean code = codeUtils.checkCode (message, check_code);
        if (!code) {
            return null;
        }

        byte [] data = BytesConvert.hexStringToBytes (message);

        // //===============消息头内容开始==========/////////////

        // 消息头长度
        int message_head = 12;

        // 消息ID
        int message_id = BytesConvert.bytes2Int (new byte [] {
                data [0], data [1]
        });
        // 消息体属性
        int message_property = BytesConvert.bytes2Int (new byte [] {
                data [2], data [3]
        });

        // 终端手机号
        String mobile = BytesConvert.encodeHexStr (new byte [] {
                data [4], data [5], data [6], data [7], data [8], data [9]
        });
        //        mobile = BytesConvert.bytes2long (new byte [] {
        //                data [4], data [5], data [6], data [7], data [8], data [9]
        //        })+"";
        Object phone = session.getAttribute ("mobilePhone");
        if (phone == null)
            session.setAttribute ("mobilePhone", new byte [] {
                    data [4], data [5], data [6], data [7], data [8], data [9]
            });

        // 消息流水号
        int message_number = BytesConvert.bytes2Int (new byte [] {
                data [10], data [11]
        });

        // 是否分包
        int package_multi = message_property & 0x2000;

        // 消息体长度
        int body_length = message_property & 0x03ff;

        // 有分包
        if (package_multi == 1) {

            message_head += 4;

            // 消息总包数
            int all_packages = BytesConvert.bytes2Int (new byte [] {
                    data [12], data [13]
            });
            // 包序号
            int package_number = BytesConvert.bytes2Int (new byte [] {
                    data [14], data [15]
            });
        }

        // 消息体
        byte [] databody = new byte [data.length - message_head];

        // 消息体长度有误
        if (body_length != databody.length) {
            return null;
        }

        // ////===============消息头内容 结束==========/////////////

        try {

            System.arraycopy (data, message_head, databody, 0, databody.length);

            BaseMessage ret = null;

            LoginDevice ld = new LoginDevice ();
            ld.setDt (new T808 ());
            ld.setSn (deviceSn);

            T808Response response = new T808Response ();// 应答消息

            response.setPhone (new byte [] {
                    data [4], data [5], data [6], data [7], data [8], data [9]
            });
            // 应答流水号
            int answer_number = 0;

            switch (message_id) {

            case 0x0001: // 终端通用应答
                ret = this.codeUtils.defaultDeviceReply (databody, deviceSn, response, session);
                break;
            case 0x0002: // 终端心跳
                codeUtils.defaultReply (session, response, data, (byte) 0);
                ret = new BaseMessage (Act.HEART_BEAT, null);
                break;
            case 0x0100: // 终端注册

                DeviceVehicle dv = codeUtils.registerDevice (databody, mobile);
                DeviceRegistRsBean registBean = this.registService.getRegistId (dv);

                // 终端注册应答    服务器消息流水号，回复消息id
                ret = new BaseMessage (Act.REG, dv);
                this.codeUtils.toMsgSecquent (response, session).toMsgId (response, BytesConvert.int2Bytes (0x8100, 2))
                        .registRs (registBean, deviceSn, databody, response, message_number);
                break;
            case 0X0003: // 终端注销 

                ret = new BaseMessage (Act.LOGOFF, null);
                boolean removeDevice = this.registService.removeDevice (deviceSn);
                // 平台通用应答消息
                this.codeUtils.defaultReply (session, response, databody, (byte) (removeDevice ? 0 : 1));
                break;
            case 0x0102: // 终端鉴权
                String auth_key = new String (databody, "UTF-8");
                deviceSn = this.registService.getDeviceSn (auth_key);
                if (deviceSn == null) {
                    this.codeUtils.defaultReply (session, response, data, (byte) 1);
                }
                else {
                    session.setAttribute (DEVICE_SN, deviceSn);//权鉴成功,记录session
                    ret = new BaseMessage (Act.AUTH, auth_key);
                    ld.setSn (deviceSn);
                    this.codeUtils.defaultReply (session, response, data, (byte) 0);
                }
                // 平台通用应答消息
                break;
            case 0x0104: // 查询终端参数应答

                DeviceParam d = codeUtils.getDeviceParam (databody);
                ret = new BaseMessage (Act.DEVICE_PARAM, d);
                break;
            case 0x0107://升级申请
                ret = this.codeUtils.toMsgSecquent (response, session)
                        .toMsgId (response, BytesConvert.int2Bytes (0x8108, 2)).upgrade (databody, response, session);
                break;
            case 0x0200: // 位置信息汇报
                Position position = codeUtils.getPositionMessage (databody);
                com.capcare.harbor.protocol.Position posiBean = new com.capcare.harbor.protocol.Position ();
                posiBean = codeUtils.toPostitionBean (deviceSn, position);
                codeUtils.addMsgNo (session);
                codeUtils.defaultReply (session, response, data, (byte) 0);
                ret = new BaseMessage (Act.POSITION, posiBean);
                break;
            case 0x0201: // 位置信息查询应答
            case 0x0500: // 车辆控制应答

                // 应答流水号
                answer_number = BytesConvert.bytes2Int (new byte [] {
                        databody [0], databody [1]
                });

                byte [] pos = new byte [databody.length - 2];
                System.arraycopy (databody, 2, pos, 0, databody.length - 2);
                // 位置信息汇报
                Position positionReply = codeUtils.getPositionMessage (pos);
                ret = new BaseMessage (Act.POSITION, positionReply);
                break;
            case 0x0301: // 事件报告
                int event_id = BytesConvert.bytes2Int (new byte [] {
                    databody [0]
                });
                ret = new BaseMessage (Act.EVENT_REPLY, event_id);

                // 平台通用应答消息
                codeUtils.defaultReply (session, response, data, (byte) 0);
                break;
            case 0x0302: // 提问应答
                // 应答流水号(对应的提问下发消息的流水号)
                answer_number = BytesConvert.bytes2Int (new byte [] {
                        databody [0], databody [1]
                });
                // 答案id(提问下发中附带的答案ID)
                int answerid = BytesConvert.bytes2Int (new byte [] {
                    databody [2]
                });

                ret = new BaseMessage (Act.QUESTION_REPLY, answerid);
                break;
            case 0x0303: // 信息点播/取消
                // 信息类型
                int message_type = BytesConvert.bytes2Int (new byte [] {
                    databody [0]
                });
                // 点播/取消标志
                int demand = BytesConvert.bytes2Int (new byte [] {
                    databody [1]
                });

                ret = new BaseMessage (Act.MESSAGE_REPLY, new Integer [] {
                        message_type, demand
                });

                // 平台通用应答消息
                codeUtils.defaultReply (session, response, data, (byte) 0);
                break;
            case 0x0700: // 行驶记录仪数据上报
                // 应答流水号
                answer_number = BytesConvert.bytes2Int (new byte [] {
                        databody [0], databody [1]
                });
                // // 命令字
                // int command = BytesConvert
                // .bytes2Int(new byte[] { databody[2] });
                // //
                // // // 数据块总长度 WORD
                // int block_length = BytesConvert.bytes2Int(new byte[] {
                // databody[3], databody[4] });
                // // 包数据长度 WORD
                // int package_length = BytesConvert.bytes2Int(new byte[] {
                // databody[5], databody[6] });

                codeUtils.getDataRecorder (databody);
                // // 数据块
                // String block = ""; // 数据块内容格式见GB/T19056中相关内容

                ret = new BaseMessage (Act.DATA_RECORDER, null);

                break;
            case 0x0701: // 电子运单上报
                // 电子运单长度
                int waybill_length = BytesConvert.bytes2Int (new byte [] {
                        databody [0], databody [1], databody [2], databody [3]
                });

                byte [] eticket = new byte [databody.length - 4];
                System.arraycopy (databody, 4, eticket, 0, eticket.length);
                // 电子运单内容
                String waybill_content = new String (eticket, "GBK");

                // 平台通用应答消息
                codeUtils.defaultReply (session, response, data, (byte) 0);
                ret = new BaseMessage (Act.WAYBILL, null);
                break;
            case 0x0702: // 驾驶员身份信息采集上报

                DriverInfo driver = codeUtils.getDriverInfo (databody);
                ret = new BaseMessage (Act.DRIVER_INFO, driver);

                // 平台通用应答消息
                codeUtils.defaultReply (session, response, data, (byte) 0);
                break;
            case 0x0800: // 多媒体事件信息上传

                // 平台通用应答消息
                codeUtils.defaultReply (session, response, data, (byte) 0);
                MediaEvent event = codeUtils.getMediaEventMessage (databody);
                ret = new BaseMessage (Act.MEDIAEVENT_UPLOAD, event);

                break;
            case 0x0801: // 多媒体数据上传

                MediaEvent dd = codeUtils.getMediaEventMessage (databody);

                byte [] base = new byte [databody.length - 8];
                System.arraycopy (databody, 8, base, 0, databody.length - 8);

                Position basePostion = codeUtils.getPositionBaseMessage (base);

                // // 数据总包数
                // int packages = BytesConvert.bytes2Int(new byte[] {
                // databody[8],
                // databody[9], databody[10], databody[11] });
                // // 包id
                // int package_id = BytesConvert
                // .bytes2Int(new byte[] { databody[12], databody[13],
                // databody[14], databody[15] });

                // 多媒体数据包
                String package_databody = "";

                ret = new BaseMessage (Act.MEDIA_UPLOAD, dd);

                // 多媒体数据上传应答消息

                break;
            case 0x0802: // 存储多媒体数据检索应答
                // 应答流水号
                answer_number = BytesConvert.bytes2Int (new byte [] {
                        databody [0], databody [1]
                });
                // 多媒体数据总项数
                int media_num = BytesConvert.bytes2Int (new byte [] {
                        databody [2], databody [3]
                });
                // // 包项数
                // int pkg_num = BytesConvert.bytes2Int(new byte[] {
                // databody[4],
                // databody[5] });

                List <MediaItem> list = new ArrayList <MediaItem> ();

                for (int i = 0; i < media_num; i++) {
                    int search_media_id = BytesConvert.bytes2Int (new byte [] {
                            databody [0], databody [1], databody [2], databody [3]
                    });
                    int search_media_type = BytesConvert.bytes2Int (new byte [] {
                        databody [4]
                    });
                    int search_channel_id = BytesConvert.bytes2Int (new byte [] {
                        databody [5]
                    });
                    int search_event_encode = BytesConvert.bytes2Int (new byte [] {
                        databody [6]
                    });

                    Position bPostion = codeUtils.getPositionBaseMessage (databody);

                    list.add (new MediaItem (search_media_id, search_media_type, search_channel_id,
                            search_event_encode, bPostion));
                }

                MediaRetrievalReply mr = new MediaRetrievalReply ();
                mr.setNum (media_num);
                mr.setItems (list);

                ret = new BaseMessage (Act.MEDIA_REPLY, mr);

                break;
            case 0x0900: // 数据上行透传

                // 透传消息类型
                int msg_type = BytesConvert.bytes2Int (new byte [] {
                    databody [0]
                });
                // // 信息总长度
                // int msg_length = BytesConvert.bytes2Int(new byte[] {
                // databody[1], databody[2], databody[3], databody[4] });
                // // 包信息长度
                // int pkg_length = BytesConvert.bytes2Int(new byte[] {
                // databody[5], databody[6] });
                // 透传消息内容
                String msg_content = "";

                ret = new BaseMessage (Act.DOWNLINK_DATA, null);

                break;
            case 0x0901: // 数据压缩上报
                // 压缩消息长度
                int message_length = BytesConvert.bytes2Int (new byte [] {
                        databody [0], databody [1], databody [2], databody [3]
                });
                String message_content = "";

                ret = new BaseMessage (Act.GZIP, null);
                break;
            case 0x0A00: // 终端RSA公钥

                int e = BytesConvert.bytes2Int (new byte [] {
                        databody [0], databody [1], databody [2], databody [3]
                });

                byte [] val = new byte [databody.length - 4];
                System.arraycopy (databody, 4, val, 0, val.length);
                String n = BytesConvert.encodeHexStr (val);

                ret = new BaseMessage (Act.RSA, new RSA (e, n));
                break;
            default:
                break;
            }

            if (ret == null) {
                logger.error ("message id : {} , devicesn : {}", message_id, deviceSn);
                return null;
            }
            ret.setLoginDevice (ld);
            ret.setResponse (response);

            return ret;
        }
        catch (NoIndexException ex) {
            logger.error ("decode", ex);
        }
        catch (Exception ex) {
            in.limit (0);
            logger.error ("decode", ex);
        }
        return null;
    }

    private boolean checkInBytes (IoBuffer in) {

        in.mark ();
        int limit = in.limit ();
        byte [] bytes = new byte [limit];
        try {
            in.get (bytes);
        }
        catch (BufferUnderflowException e) {

            in.reset ();
            return false;
        }
        in.reset ();

        logger.info (" t808 receive msg：[" + BytesConvert.encodeHexStr (bytes) + "]");
        return true;
    }

}
