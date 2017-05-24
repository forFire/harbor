package com.capcare.harbor.handler.device.beidou;

import com.capcare.harbor.handler.device.DeviceType;

public class Beidou extends DeviceType {

    public Beidou () {

        super ("BEIDOU", "BEIDOU", new byte [] {
                0x67, 0x67
        });
    }
}
