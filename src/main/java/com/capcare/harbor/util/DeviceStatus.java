/**
 * 
 */
package com.capcare.harbor.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zf
 * 设备状态  -
 */
@SuppressWarnings("serial")
public class DeviceStatus {
	
	private static Map<String, String> statusMap=null;
	
	static
	{
	   //初始化Map
		 statusMap = new HashMap<String, String>(){{
		   
	        put("115", "预留");
	        put("114", "预留");
	        put("18", "电源故障(欠压)");
	        put("17", "延时状态");
	        put("16", "反馈");
	        put("15", "启动(开启)");
	        put("14", "监管");
	        put("13", "屏蔽(隔离)");
	        put("12", "故障");
	        put("11", "火警");
	        put("10", "正常状态");
	        
	        put("1008", "电源恢复");
	        put("1007", "延时结束");
	        put("1006", "反馈恢复");
	        put("1005", "停动(关闭)");
	        put("1004", "监管恢复");
	        put("1003", "取消屏蔽(释放)");
	        put("1002", "故障恢复");
	        put("1001", "火警恢复");
	        put("1000", "测试状态");
	        
	        put("1018", "预留");
	        put("1017", "报警");
	        put("1016", "预警");
	        put("1015", "动作");
	        put("1014", " ");
	        put("1013", "离线");
	        put("1012", "自动火警(有人)");
	        put("1011", "确认火警");
	        put("1010", "探头测试");
	        
	        put("1028", "停用");
	        put("1027", " ");
	        put("1026", "预警恢复");
	        put("1025", "停止");
	        put("1024", " ");
	        put("1023", "离线恢复");
	        put("1022", "火警超时(有人)");
	        put("1021", "误报");
	        put("1020", "测试结束");
	        
	        
	    }};
	}
	
	public String returnValue(String key){
		return statusMap.get(key);
	}
	
}
