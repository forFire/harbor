package module.jsonp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * @author Junyan
 */
public class FilterServletOutputStream extends ServletOutputStream {

	private DataOutputStream stream;

	public FilterServletOutputStream(OutputStream output) {
		stream = new DataOutputStream(output);
	}

	@Override
	public void write(int b) throws IOException {
		stream.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		super.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
	}

}
