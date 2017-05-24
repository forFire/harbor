package module.util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author fyq
 */
public class AbsolutePath {
	public static URI absolutePath(String relativePath) {
		try {
			if (!relativePath.contains("../")) {
				return AbsolutePath.class.getClassLoader().getResource(relativePath).toURI();
			}
			// 防止前面加"/"类型的不规范的参数
			if (relativePath.substring(0, 1).equals("/")) {
				relativePath = relativePath.substring(1);
			}
			String classPath = AbsolutePath.class.getClassLoader().getResource("").toString();
			// 后面部分
			String back = relativePath.substring(relativePath.lastIndexOf("../") + 3);
			// 前面部分,再得有多少级别
			String front = relativePath.substring(0, relativePath.lastIndexOf("../") + 3);
			int containSum = AbsolutePath.containSum(front, "../");
			// 砍掉后的
			String cutPath = AbsolutePath.cutPath(classPath, "/", containSum);
			String absolutePath = cutPath + back;
			return new URI(absolutePath);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	// 返回多少级；如containSum("../../","../")，因为含有两个“../”，则返回2
	private static int containSum(String front, String tag) {
		int containSum = 0;
		int tagLength = tag.length();
		while (front.contains(tag)) {
			containSum = containSum + 1;
			front = front.substring(tagLength);
		}
		return containSum;
	}

	// 返回砍掉sum级后的classPath；如cutPath("c:/a/b/c/", "/", 2),则返回c:/a
	private static String cutPath(String cp, String separator, int sum) {
		for (int i = 0; i < sum; i++) {
			// 减2的原因是:传过来的classPath最后有个斜杠分隔符
			cp = cp.substring(0, cp.lastIndexOf(separator, cp.length() - 2) + 1);
		}
		return cp;
	}

}
