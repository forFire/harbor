package com.capcare.harbor.handler.device.m2616;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.capcare.harbor.vo.InstructType;

public class InstructMapping {

	private static Map<Integer,InstructType> device_to_capcare = new HashMap<Integer,InstructType>();
	
	private static Map<InstructType,Integer> capcare_to_device = new HashMap<InstructType,Integer>();
	
	private static DecimalFormat format = new DecimalFormat("000");
	
	static{
		device_to_capcare.put(0, InstructType.Reboot);
        device_to_capcare.put(10, InstructType.Upgrade);
		device_to_capcare.put(20, InstructType.Restore);
		device_to_capcare.put(710, InstructType.SetSos);
		device_to_capcare.put(740, InstructType.SetSpeed);
		device_to_capcare.put(750, InstructType.SetFenceSwitch);
		device_to_capcare.put(752, InstructType.SetFence);
		device_to_capcare.put(751, InstructType.SetFenceStatus);
		device_to_capcare.put(730, InstructType.SetTickInterval);
		device_to_capcare.put(760, InstructType.SetMoveSwitch);
		device_to_capcare.put(111, InstructType.ClearAlarm);
		
		for(Integer key : device_to_capcare.keySet()){
			InstructType value = device_to_capcare.get(key);
			capcare_to_device.put(value, key);
		}
	}
	
	protected static InstructType mapToCapcare(int orig_instruct){
		return device_to_capcare.get(orig_instruct);
	}
	
	protected static String mapToDevice(InstructType capcare_instruct){
		return format.format(capcare_to_device.get(capcare_instruct));
	}
}
