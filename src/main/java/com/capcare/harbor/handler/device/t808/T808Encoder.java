package com.capcare.harbor.handler.device.t808;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import module.util.BytesConvert;
import module.util.Tools;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.vo.FenceVo;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.InstructVo;

@Component
@Scope("singleton")
public class T808Encoder {

    private final Logger        log       = LoggerFactory.getLogger (this.getClass ());

    @Resource
    private T808EncoderUtils    encodeUtls;

    private static final String DEVICE_SN = "deviceSn";

    private static final String defaultSn = null;

    /**
     * 将服务器端指令 转换为 为设备需要的格式
     * 
     * @param session
     * @param protocol
     * @return
     * @throws Exception
     */
    public Object encodeInstruct (IoSession session, InstructVo protocol) throws Exception {

        List <T808Message> encodeInstructs = this.encodeInstructs (session, protocol);
        List <byte []> rsls = new ArrayList <byte []> ();
        for (T808Message msg : encodeInstructs) {
            rsls.add (msg.getBytes ());
        }
        return rsls;
    }

    private List <T808Message> encodeInstructs (IoSession session, InstructVo protocol) throws Exception {

        List <T808Message> encodeInstructs = new ArrayList <T808Message> ();
        String deviceSn = session.getAttribute (DEVICE_SN) == null ? defaultSn : session.getAttribute (DEVICE_SN)
                .toString ();

        Map <String, Object> cmdMap = protocol.getCmdMap ();
        InstructType instructType = protocol.getInstructType ();

        T808Message message = new T808Message ();

        encodeInstructs.add (message);
        byte [] phoneBytes = (byte []) session.getAttribute ("mobilePhone");
        message.setPhone (phoneBytes);

        Integer messageId = 0;

        log.info ("sn : {} , encodeType : {} typeNum : {}", new String [] {
                deviceSn, instructType.getInfo (), "" + instructType.getNum ()
        });
        switch (instructType) {
        case SetTickInterval:
            messageId = 0x8103;
            this.encodeUtls.toMsgSecquent (message, session).toMsgId (message, messageId);
            this.encodeUtls.setTickInterval (cmdMap, message, deviceSn, instructType.getNum ());
            break;
        case SetSpeed:
            messageId = 0x8103;
            this.encodeUtls.toMsgSecquent (message, session).toMsgId (message, messageId);
            this.encodeUtls.setSpeed (cmdMap, message, deviceSn, instructType.getNum ());
            break;
        case SetSos:
            this.encodeUtls.sosAdd (cmdMap);
            InstructVo protocolPhone = new InstructVo ();
            protocolPhone.setDeviceSn (deviceSn);
            protocolPhone.setInstructType (InstructType.SetPhoneBook);
            protocolPhone.setCmdMap (cmdMap);
            encodeInstructs.addAll (this.encodeInstructs (session, protocolPhone));
        case SetDeviceParam:// 设置终端参数
            messageId = 0x8103;
            this.encodeUtls.toMsgSecquent (message, session).toMsgId (message, messageId);
            this.encodeUtls.setDeviceParam (cmdMap, message, deviceSn, instructType.getNum ());
            //查询终端参数
            InstructVo protocolQuery = new InstructVo ();
            protocolQuery.setDeviceSn (deviceSn);
            protocolQuery.setInstructType (InstructType.QueryDeviceParam);
            encodeInstructs.addAll (this.encodeInstructs (session, protocolQuery));
            break;
        case SetMoveSwitch:
            messageId = 0x8103;
            this.encodeUtls.toMsgSecquent (message, session).toMsgId (message, messageId);
            this.encodeUtls.setMoveSwitch (cmdMap, message, deviceSn, instructType.getNum ());
            break;
        case QueryDeviceParam:// 查询终端参数
            messageId = 0x8104;
            this.encodeUtls.toMsgSecquent (message, session).toMsgId (message, messageId);
            break;
        case DeviceControl:// 终端控制
            messageId = 0x8105;
            this.encodeUtls.toMsgSecquent (message, session).toMsgId (message, messageId);
            this.encodeUtls.deviceCommand (message, cmdMap);
            break;
        case QueryPosition:
            messageId = 0x8201;
            this.encodeUtls.toMsgSecquent (message, session).toMsgId (message, messageId);
            break;
        case PositionTrack:// 临时位置跟踪控制
            messageId = 0x8202;
            this.encodeUtls.toMsgSecquent (message, session).toMsgId (message, messageId);
            // 时间间隔
            int time_interval = Tools.toInt (cmdMap.get ("time_interval"));

            if (time_interval > 0) {
                // 位置跟踪有效期
                int expiry_date = Tools.toInt (cmdMap.get ("expiry_date"));
                message.setBodyLength (6);
                message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (time_interval, 2),
                        BytesConvert.int2Bytes (expiry_date, 4)));
            }
            else {
                message.setBodyLength (2);
                message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (time_interval, 2)));
            }

            break;
        case IssueText:// 文本信息下发
            messageId = 0x8300;
            // 标志
            int mark = (Integer) cmdMap.get ("mark");
            // 文本信息
            String content = (String) cmdMap.get ("content");

            message.setBodyLength (content.length () + 1);
            message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (mark, 1), content.getBytes ()));

            break;
        case SetEvent:// 事件设置
            messageId = 0x8301;
            this.encodeUtls.setEvent (cmdMap, message);
            break;
        case IssueQuestion:// 提问下发
            messageId = 0x8302;
            this.encodeUtls.issueQuestion (cmdMap, message);
            break;
        case SetDemandMenu:// 信息点播菜单设置
            messageId = 0x8303;
            this.encodeUtls.setDemandMenu (cmdMap, message);
            break;
        case MessageService:// 信息服务
            messageId = 0x8304;
            // 信息类型 BYTE
            int service_message_type = (Integer) cmdMap.get ("message_type");
            // // 包信息长度 WORD
            // int service_package_length = (Integer)
            // cmdMap.get("package_length");

            // 信息内容 STRING
            String service_message_content = (String) cmdMap.get ("message_content");
            // 信息长度 WORD
            int service_message_length = service_message_content.length ();

            message.setBodyLength (service_message_length + 3);
            message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (service_message_type, 1),
                    BytesConvert.int2Bytes (service_message_length, 2), service_message_content.getBytes ()));

            break;
        case PhoneCallBack:// 电话回拨消息
            messageId = 0x8400;
            // 标志
            int phone_mark = (Integer) cmdMap.get ("mark");
            // 电话号码
            String phone = (String) cmdMap.get ("phone");

            message.setBodyLength (phone.length () + 1);
            message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (phone_mark, 1), phone.getBytes ()));

            break;
        case SetPhoneBook:// 设置电话本消息
            messageId = 0x8401;
            this.encodeUtls.setPhoneBook (cmdMap, message);
            break;
        case CarControl:// 车辆控制
            messageId = 0x8500;
            int control_type = (Integer) cmdMap.get ("control_type");

            message.setBodyLength (1);
            message.setMessageBody (BytesConvert.concatAll (BytesConvert.int2Bytes (control_type, 1)));

            break;
        case SetFenceSwitch:
            int switchType = Tools.toInt (cmdMap.get ("switch"));
            messageId = 0x8103;
            this.encodeUtls.toMsgSecquent (message, session).toMsgId (message, messageId);
            List <T808Message> fenceSwitchLs = this.encodeUtls.fenceSwitch (cmdMap, message, deviceSn,
                    instructType.getNum (), session, switchType);
            encodeInstructs.addAll (fenceSwitchLs);
            break;
        case SetFence:// 设置围栏
            this.encodeUtls.toMsgSecquent (message, session);
            messageId = this.encodeUtls.setFence (cmdMap, message, deviceSn, instructType.getNum ());
            this.encodeUtls.toMsgId (message, messageId);
            break;
        case DeleteFence:// 删除围栏

            int ftype = (Integer) cmdMap.get ("fence_type");
            @SuppressWarnings("unchecked")
            List <Integer> del_fences = (List <Integer>) cmdMap.get ("fences");

            message.setBodyLength (del_fences.size () * 4 + 1);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                    BytesConvert.int2Bytes (del_fences.size (), 1)));

            for (Integer f : del_fences) {
                message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                        BytesConvert.int2Bytes (f, 4)));
            }

            if (ftype == FenceVo.TYPE_CIRCLE) {
                messageId = 0x8601;
            }
            else if (ftype == FenceVo.TYPE_RECTANGLE) {
                messageId = 0x8603;
            }
            else if (ftype == FenceVo.TYPE_POLYGON) {
                messageId = 0x8605;
            }
            else if (ftype == FenceVo.TYPE_LINE) {
                messageId = 0x8607;
            }

            break;
        case DrivingRecordCollect:
            /** 行驶记录数据采集命令 */
            messageId = 0x8700;

            // 标志
            int collect = (Integer) cmdMap.get ("command");

            message.setBodyLength (1);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                    BytesConvert.int2Bytes (collect, 1)));

            break;

        case DrivingRecordDownload:
            /** 行驶记录参数下传命令 */
            messageId = 0x8701;
            this.encodeUtls.downloadDriveParam (cmdMap, message);

            break;
        case CamaraShoot:
            /** 摄像头立即拍摄命令 */

            messageId = 0x8801;

            // 0 通道ID BYTE
            int cid = (Integer) cmdMap.get ("channel_id");
            // 1 拍摄命令 WORD
            int shoot = (Integer) cmdMap.get ("shoot");
            // 3 拍照间隔，录像时间 WORD
            int shoot_time = (Integer) cmdMap.get ("shoot_time");
            // 5 保持标志 BYTE
            int save = (Integer) cmdMap.get ("save");
            // 6 分辨率 BYTE
            int resolution = (Integer) cmdMap.get ("resolution");
            // 7 图像/视频质量 BYTE
            int quality = (Integer) cmdMap.get ("quality");
            // 8 亮度 BYTE
            int brilliance = (Integer) cmdMap.get ("brilliance");
            // 9 对比度 BYTE
            int contrast = (Integer) cmdMap.get ("contrast");
            // 10 饱和度 BYTE
            int saturation = (Integer) cmdMap.get ("saturation");
            // 11 色度 BYTE
            int chroma = (Integer) cmdMap.get ("chroma");

            message.setBodyLength (12);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (), BytesConvert.int2Bytes (cid, 1),
                    BytesConvert.int2Bytes (shoot, 2), BytesConvert.int2Bytes (shoot_time, 2),
                    BytesConvert.int2Bytes (save, 1), BytesConvert.int2Bytes (resolution, 1),
                    BytesConvert.int2Bytes (quality, 1), BytesConvert.int2Bytes (brilliance, 1),
                    BytesConvert.int2Bytes (contrast, 1), BytesConvert.int2Bytes (saturation, 1),
                    BytesConvert.int2Bytes (chroma, 1)));
            break;
        case MediaRetrieval:
            /** 存储多媒体数据检索 */

            messageId = 0x8802;

            // 多媒体类型 BYTE
            int media_type = (Integer) cmdMap.get ("media_type");
            // 通道ID BYTE
            int channel_id = (Integer) cmdMap.get ("channel_id");
            // 事件项编码 BYTE
            int event_type = (Integer) cmdMap.get ("event_type");
            // 起始时间 BCD[6]
            String begin_time = (String) cmdMap.get ("begin_time");
            // 结束时间 BCD[6]
            String end_time = (String) cmdMap.get ("end_time");

            message.setBodyLength (15);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                    BytesConvert.int2Bytes (media_type, 1), BytesConvert.int2Bytes (channel_id, 1),
                    BytesConvert.int2Bytes (event_type, 1), BytesConvert.str2Bcd (begin_time),
                    BytesConvert.str2Bcd (end_time)));

            break;
        case MediaUpload:
            /** 存储多媒体数据上传命令 */

            messageId = 0x8803;

            int upload_media_type = (Integer) cmdMap.get ("media_type");
            int upload_channel_id = (Integer) cmdMap.get ("channel_id");
            int upload_event_type = (Integer) cmdMap.get ("event_type");
            String upload_begin_time = (String) cmdMap.get ("begin_time");
            String upload_end_time = (String) cmdMap.get ("end_time");
            int upload_flag = (Integer) cmdMap.get ("flag");

            message.setBodyLength (16);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                    BytesConvert.int2Bytes (upload_media_type, 1), BytesConvert.int2Bytes (upload_channel_id, 1),
                    BytesConvert.int2Bytes (upload_event_type, 1), BytesConvert.str2Bcd (upload_begin_time),
                    BytesConvert.str2Bcd (upload_end_time), BytesConvert.int2Bytes (upload_flag, 1)));

            break;
        case Record:
            /** 录音开始命令 */
            messageId = 0x8804;
            // 录音命令 BYTE
            int record_type = (Integer) cmdMap.get ("record_type");
            // 录音时间 WORD
            int record_time = (Integer) cmdMap.get ("record_time");
            // 保存标志 BYTE
            int record_save = (Integer) cmdMap.get ("record_save");
            // 音频采样率 BYTE
            int samping_rate = (Integer) cmdMap.get ("samping_rate");

            message.setBodyLength (5);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                    BytesConvert.int2Bytes (record_type, 1), BytesConvert.int2Bytes (record_time, 2),
                    BytesConvert.int2Bytes (record_save, 1), BytesConvert.int2Bytes (samping_rate, 1)));

            break;

        case MediaUploadSingle:
            /** 单条存储多媒体数据检索上传命令 */
            messageId = 0x8805;

            int media_id = (Integer) cmdMap.get ("media_id");
            int del = (Integer) cmdMap.get ("flag");

            message.setBodyLength (5);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (),
                    BytesConvert.int2Bytes (media_id, 4), BytesConvert.int2Bytes (del, 1)));
            break;
        case DownlinkData:// 数据下行透传
            messageId = 0x8900;
            // 透传消息类型 BYTE
            @SuppressWarnings("unused")
            int message_type = (Integer) cmdMap.get ("message_type");
            // // 信息总长度 DWORD
            // int message_length = (Integer) cmdMap.get("message_length");
            // // 包信息长度 WORD
            // int package_length = (Integer) cmdMap.get("package_length");
            // 透传消息内容
            @SuppressWarnings("unused")
            String message_content = (String) cmdMap.get ("message_content");

            break;
        case RsaPublicKey:// 平台公钥

            messageId = 0x8A00;

            int e = (Integer) cmdMap.get ("e");
            String n = (String) cmdMap.get ("n");

            message.setBodyLength (130);
            message.setMessageBody (BytesConvert.concatAll (message.getMessageBody (), BytesConvert.int2Bytes (e, 2),
                    n.getBytes ()));
            break;
        default:
            break;
        }
        for (T808Message msg : encodeInstructs) {

            if (msg.getMessageId () == null)
                this.encodeUtls.toMsgId (msg, messageId);
            if (msg.getMessageSequnce () == null)
                this.encodeUtls.toMsgSecquent (msg, session);
            msg.setPhone (session.getAttribute ("mobilePhone") == null ? new byte [] {
                    0, 0, 0, 0, 0, 0
            } : (byte []) session.getAttribute ("mobilePhone"));
        }
        return encodeInstructs;
    }
}
