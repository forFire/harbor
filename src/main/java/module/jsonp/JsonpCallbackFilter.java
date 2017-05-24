package module.jsonp;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//<!-- web.xml JSONP Callback -->
//<filter>
//	<filter-name>jsonpCallbackFilter</filter-name>
//	<filter-class>common.jsonp.JsonpCallbackFilter</filter-class>
//</filter>
//<filter-mapping>
//	<filter-name>jsonpCallbackFilter</filter-name>
//	<url-pattern>/*</url-pattern>
//</filter-mapping>
public class JsonpCallbackFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(JsonpCallbackFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		logRequestInfo((HttpServletRequest) request);
		String cbName = request.getParameter("callback");
		if (cbName != null) {
			OutputStream out = response.getOutputStream();

			GenericResponseWrapper wrapper = new GenericResponseWrapper(
					(HttpServletResponse) response);
			chain.doFilter(request, wrapper);

			out.write(new String(cbName + "(").getBytes());
			out.write(wrapper.getData());
			out.write(new String(");").getBytes());

			wrapper.setContentType("text/javascript;charset=UTF-8");
			out.close();
		} else {
			chain.doFilter(request, response);
		}
		log.info("logicOver");
	}

	@SuppressWarnings("rawtypes")
	private void logRequestInfo(HttpServletRequest request) {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{'uop': {");
		strBuff.append("'service: '");
		strBuff.append(request.getRequestURL());
		strBuff.append("'");

		Map paramMap = request.getParameterMap();
		if (paramMap != null) {
			for (Object param : paramMap.entrySet()) {
				Entry entry = (Entry) param;
				strBuff.append(", '");
				strBuff.append(entry.getKey());
				strBuff.append("': '");
				Object value = entry.getValue();
				if (value.getClass().isArray()) {
					strBuff.append(Array.get(value, 0));
				} else {
					strBuff.append(value);
				}

				strBuff.append("'");
			}
		}

		strBuff.append("}");
		strBuff.append(", 'client':{'address':'");
		strBuff.append(request.getRemoteAddr());
		strBuff.append("'}}");

		log.info(strBuff.toString());
	}

}
