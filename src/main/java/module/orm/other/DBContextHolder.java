package module.orm.other;

public class DBContextHolder {

	private static final String MASTER = "master";  
    private static final String SLAVE = "slave";  
      
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();  
      
    public static void useMaster() {  
        contextHolder.set(MASTER);  
    }  
    
    public static void useSlave() {  
        contextHolder.set(SLAVE);  
    }
      
    public static String getDBType() {  
        return contextHolder.get();  
    }  
      
    public static void clearDBType() {  
        contextHolder.remove();  
    }  
}
