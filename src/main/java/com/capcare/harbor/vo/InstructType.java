package com.capcare.harbor.vo;

public enum InstructType {

    /** 设备自动上传 */
    AutoUp (0, "设备自动上传"),

    /** 设置围栏 */
    SetFence (1, "设置围栏"),

    /** 设置上传间隔 */
    SetTickInterval (2, "设置上传间隔"),

    /** 超速 最大值-超速开/关 */
    SetSpeed (3, "超速 最大值-超速开/关"),

    /** 设置sos号码 */
    SetSos (4, "设置sos号码"),

    /** 重启 */
    Reboot (5, "重启"),

    /** 恢复出厂 */
    Restore (6, "恢复出厂"),

    /** 围栏开关 */
    SetFenceSwitch (7, "围栏开关"),

    /** 移动开关 */
    SetMoveSwitch (8, "移动开关"),

    /** 设置围栏状态 */
    SetFenceStatus (9, "设置围栏状态"),

    /**清除告警 */
    ClearAlarm (10, "清除告警"),

    /**清除告警 */
    ClearOBDError (11, "清除OBD故障码"),
    /**固件升级 */
    Upgrade(12, "固件升级"),

    // ////////////--------T808协议------------//////////////////

    /** 设置终端参数 */
    SetDeviceParam (111, "设置终端参数"),

    /** 查询终端参数 */
    QueryDeviceParam (112, "查询终端参数"),

    /** 终端控制 */
    DeviceControl (113, "终端控制"),

    /** 位置信息查询 */
    QueryPosition (114, "位置信息查询"),

    /** 临时位置跟踪控制 */
    PositionTrack (115, "临时位置跟踪控制"),

    /** 事件设置 */
    SetEvent (116, "事件设置"),

    /** 提问下发 */
    IssueQuestion (117, "提问下发"),

    /** 信息点播菜单设置 */
    SetDemandMenu (118, "信息点播菜单设置"),

    /** 信息服务 */
    MessageService (119, "信息服务"),

    /** 电话回拨消息 */
    PhoneCallBack (120, "电话回拨消息"),

    /** 设置电话本消息 */
    SetPhoneBook (121, "设置电话本消息"),

    /** 车辆控制 */
    CarControl (122, "车辆控制"),

    /** 行驶记录数据采集命令 */
    DrivingRecordCollect (123, "行驶记录数据采集命令"),

    /** 行驶记录参数下传命令 */
    DrivingRecordDownload (124, "行驶记录参数下传命令"),

    /** 存储多媒体数据检索 */
    MediaRetrieval (125, "存储多媒体数据检索"),

    /** 存储多媒体数据上传命令 */
    MediaUpload (126, "存储多媒体数据上传命令 "),

    /** 录音开始命令 */
    Record (127, "录音开始命令 "),

    /** 数据下行透传 */
    DownlinkData (128, "数据下行透传 "),

    /** 平台公钥 */
    RsaPublicKey (129, "平台公钥  "),

    /** 摄像头立即拍摄命令 */
    CamaraShoot (130, "摄像头立即拍摄命令"),

    /** 文本信息下发 */
    IssueText (131, "文本信息下发"),

    /** 删除围栏 */
    DeleteFence (134, "删除围栏"),

    /** 单条存储多媒体数据检索上传命令 */
    MediaUploadSingle (135, "单条存储多媒体数据检索上传命令"),

    /** 发送普通通信短报文消息 */
    SentMessage (2001, "发送普通通信短报文消息"),
    
    /** 发送友邻位置短报文消息 */
    SentNeigbourMessage (2002, "发送友邻位置短报文消息"),
    
    /** 发送紧急状态设备短报文消息 */
    SentExigencyMessage (2003, "发送紧急状态设备短报文消息"),
    
    /** 请求指挥机信息 */
    RequestUserData (2000, "请求指挥机信息"),
    
    /** 请求指挥机功况信息 */
    RequestStatus (2000, "请求指挥机功况信息 "),
    
    /** 请求GPS数据 */
    RequestGPS (2000, "请求GPS数据")
    
    ;

    private int    num;

    private String info;

    private InstructType (int num, String info) {

        this.num = num;
        this.info = info;
    }

    public int getNum () {

        return this.num;
    }

    public String getInfo () {

        return this.info;
    }

    public static InstructType getByNum (int num) {

        for (InstructType it : InstructType.values ()) {
            if (it.getNum () == num) {
                return it;
            }
        }
        return null;
    }

}
