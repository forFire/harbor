package module.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpContent {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpContent.class);
	
	public static String get(String url) {
		String content = null;
		HttpGet httpGet = new HttpGet(url);
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter("http.socket.timeout", 2000);
		httpClient.getParams().setParameter("http.connection.timeout", 2000);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				content = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity);
			}
			return content;
		} catch (Exception e) {
			logger.error("",e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return null;
	}

	/**
	 * 
	 * 获取https请求结果
	 * 
	 * @date 2013-3-20,上午11:17:48
	 * @author fyq
	 */
	public static String httpsGet(String url) {
		// 响应内容
		String content = null;
		// 创建默认的httpClient实例
		HttpClient httpClient = new DefaultHttpClient();
		// 创建TrustManager
		X509TrustManager xtm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		try {
			// TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者
			// 但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");
			// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			// 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry()
					.register(new Scheme("https", 443, socketFactory));
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			// 获取响应实体
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				content = EntityUtils.toString(entity, "UTF-8");
				// Consume response content
				EntityUtils.consume(entity);
			}
			return content;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally {
			httpClient.getConnectionManager().shutdown(); // 关闭连接,释放资源
		}
	}

	public static void main(String[] args) {
		String reqURL = "https://api.weibo.com/2/account/get_uid.json";
		reqURL += "?access_token=2.00WkqDWDP3IJXD64ad679760J3u6CC";
		System.out.println(HttpContent.httpsGet(reqURL));
	}
}
