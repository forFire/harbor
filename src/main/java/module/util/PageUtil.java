package module.util;

/**
 * @author Junyan
 * 
 */
public class PageUtil {

	public static int getPageStart(int pageNumber, int pageSize) {
		return (pageNumber - 1) * pageSize;
	}

	public static int getPageStart(int totalCount, int pageNumber, int pageSize) {
		int start = (pageNumber - 1) * pageSize;
		if (start >= totalCount) {
			start = 0;
		}
		return start;
	}

}
