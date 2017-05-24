package com.capcare.harbor.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * @author fyq
 */
public class CharsetKey {

	/** UTF-8 */
	public static String DEFAULT_CHARSET_NAME = "UTF-8";

	/** UTF-8 */
	public static Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);

	/** UTF-8 */
	public static CharsetDecoder DEFAULT_DECODER = DEFAULT_CHARSET.newDecoder();

}
