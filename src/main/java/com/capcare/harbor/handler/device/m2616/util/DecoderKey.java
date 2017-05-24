package com.capcare.harbor.handler.device.m2616.util;

/**
 * @author fyq
 */
public class DecoderKey {

    /** # 0x23 */
    public static final byte   M2616_BEGIN  = '#';

    /** $ 0x24 */
    public static final byte   MT90_BEGIN   = '$';

    /** < 0x3c */
    public static final byte   APP_BEGIN    = '<';

    public static final String BEIDOU_BEGIN = "@";

    /** 心跳符 空格 */
    public static final String BEAT         = " ";

    // ---------------------可整理掉
    /** 1 */
    public static final byte   M2616        = 1;

    /** 2 */
    public static final byte   MT90         = 2;

    /** 3 */
    public static final byte   phone        = 3;
    // ---------------------可整理掉

}
