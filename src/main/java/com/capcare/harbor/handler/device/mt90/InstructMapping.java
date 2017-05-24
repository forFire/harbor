package com.capcare.harbor.handler.device.mt90;

import java.util.HashMap;
import java.util.Map;

import com.capcare.harbor.vo.InstructType;

public class InstructMapping {

	private static Map<String,InstructType> device_to_capcare = new HashMap<String,InstructType>();	
	private static Map<InstructType,String> capcare_to_device = new HashMap<InstructType,String>();

	static{
		device_to_capcare.put("F11", InstructType.Restore);
		device_to_capcare.put("A71", InstructType.SetSos);
		device_to_capcare.put("B07", InstructType.SetSpeed);
		device_to_capcare.put("B05", InstructType.SetFence);
		device_to_capcare.put("A12", InstructType.SetTickInterval);
		
		for(String key : device_to_capcare.keySet()){
			InstructType value = device_to_capcare.get(key);
			capcare_to_device.put(value, key);
		}
	}
	
	protected static InstructType mapToCapcare(String orig_instruct){
		return device_to_capcare.get(orig_instruct);
	}
	
	protected static String mapToDevice(InstructType capcare_instruct){
		return capcare_to_device.get(capcare_instruct);
	}
}
