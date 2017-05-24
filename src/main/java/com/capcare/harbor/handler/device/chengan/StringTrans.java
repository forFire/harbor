package com.capcare.harbor.handler.device.chengan;

public class StringTrans {

	private byte[] array;
	private int pos;
	private int end;
	private int saved_pos;
	private int saved_end;
	public StringTrans(byte[] input) {
		array = input;
		pos = 0;
		end = array.length;
		saved_pos = -1;
		saved_end = -1;
	}

	// 复制byte数组
	public byte[] copyBytes(int len) throws Exception {// 21
														// gbk
		require(len);
		byte[] out = new byte[len];
		System.arraycopy(array, pos, out, 0, len);
		pos += len;
		return out;
	}

	public int current() {
		return pos;
	}

	/**
	 * Returns the number of bytes that can be read from this stream before
	 * reaching the end.
	 */
	public int remaining() {
		return end - pos;
	}

	private void require(int n) throws Exception {
		if (n > remaining()) {
			throw new Exception("end of input");
		}
	}

	/**
	 * Marks the following bytes in the stream as active.
	 * 
	 * @param len
	 *            The number of bytes in the active region.
	 * @throws IllegalArgumentException
	 *             The number of bytes in the active region is longer than the
	 *             remainder of the input.
	 */
	public void setActive(int len) {
		if (len > array.length - pos) {
			throw new IllegalArgumentException("cannot set active " + "region past end of input");
		}
		end = pos + len;
	}

	/**
	 * Clears the active region of the string. Further operations are not
	 * restricted to part of the input.
	 */
	public void clearActive() {
		end = array.length;
	}

	/**
	 * Resets the current position of the input stream to the specified index,
	 * and clears the active region.
	 * 
	 * @param index
	 *            The position to continue parsing at.
	 * @throws IllegalArgumentException
	 *             The index is not within the input.
	 */
	public void jump(int index) {
		/**
		 * end 27 saved_end -1 pos 0 saved_pos -1 array[] index:1
		 */
		if (index >= array.length) {
			throw new IllegalArgumentException("cannot jump past " + "end of input");
		}
		pos = index;
		end = array.length;
	}

	/**
	 * Saves the current state of the input stream. Both the current position
	 * and the end of the active region are saved.
	 * 
	 * @throws IllegalArgumentException
	 *             The index is not within the input.
	 */
	public void save() {
		saved_pos = pos;
		saved_end = end;
	}

	/**
	 * Restores the input stream to its state before the call to {@link #save}.
	 */
	public void restore() {
		if (saved_pos < 0) {
			throw new IllegalStateException("no previous state");
		}
		pos = saved_pos;
		end = saved_end;
		saved_pos = -1;
		saved_end = -1;
	}

	/**
	 * Reads a byte array of a specified length from the stream into an existing
	 * array.
	 * 
	 * @param b
	 *            The array to read into.
	 * @param off
	 *            The offset of the array to start copying data into.
	 * @param len
	 *            The number of bytes to copy.
	 * @throws Exception
	 *             The end of the stream was reached.
	 */
	public void readByteArray(byte[] b, int off, int len) throws Exception {
		require(len);
		System.arraycopy(array, pos, b, off, len);
		pos += len;
	}

	/**
	 * Reads a byte array of a specified length from the stream.
	 * 
	 * @return The byte array.
	 * @throws Exception
	 *             The end of the stream was reached.
	 */
	public byte[] readByteArray(int len) throws Exception {
		require(len);
		byte[] out = new byte[len];
		System.arraycopy(array, pos, out, 0, len);
		pos += len;
		// System.out.println(Arrays.toString(out));
		return out;
	}

	/**
	 * Reads a byte array consisting of the remainder of the stream (or the
	 * active region, if one is set.
	 * 
	 * @return The byte array.
	 */
	public byte[] readByteArray() {
		int len = remaining();
		byte[] out = new byte[len];
		System.arraycopy(array, pos, out, 0, len);
		pos += len;
		return out;
	}

	/**
	 * Reads a counted string from the stream. A counted string is a one byte
	 * value indicating string length, followed by bytes of data.
	 * 
	 * @return A byte array containing the string.
	 * @throws Exception
	 *             The end of the stream was reached.
	 */
	public byte[] readCountedString() throws Exception {
		require(1);
		int len = array[pos++] & 0xFF;
		return readByteArray(len);
	}

	//

	public static String bcd2Str(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString();
	}

	public static String ascStr(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			temp.append((char) Integer.parseInt(bytes[i] + ""));
		}
		return temp.toString();
	}

	/**
	 * Reads an 8 bit value from the stream, as an Integer1.
	 * 
	 * @return An Integer1 value.
	 * @throws Exception
	 *             The end of the stream was reached.
	 */
	public String readInteger1() throws Exception {
		require(1);
		int value = (array[pos++] & 0xFF);
		return "" + value;
	}

	public byte readByte() throws Exception {
		require(1);
		return array[pos++];
	}

	/**
	 * Reads a 16 bit value from the stream, as an Integer2.
	 * 
	 * @param direction
	 * 
	 * @return An Integer2 value.
	 * @throws Exception
	 *             The end of the stream was reached.
	 */
	public String readInteger2(String direction) throws Exception {
		// direction = "L2H";
		// if(direction != null && direction.equalsIgnoreCase("H2L"))
		// direction = "H2L";

		require(2);
		int b1 = array[pos++] & 0xFF;
		int b2 = array[pos++] & 0xFF;

		int value = 0;
		if (direction.equalsIgnoreCase("L2H"))
			value = ((b2 << 8) + b1);
		else
			value = ((b1 << 8) + b2);

		return "" + value;
	}

	/**
	 * Reads a 32 bit value from the stream, as a Integer4.
	 * 
	 * @param direction
	 * 
	 * @return An Integer4 value.
	 * @throws Exception
	 *             The end of the stream was reached.
	 */
	public String readInteger4(String direction) throws Exception {// H2L
		require(4);
		int b1 = array[pos++] & 0xFF;// 0
		int b2 = array[pos++] & 0xFF;// 0
		int b3 = array[pos++] & 0xFF;// 0
		int b4 = array[pos++] & 0xFF;// 90

		long value = 0;
		if (direction.equalsIgnoreCase("L2H"))
			value = (((long) b4 << 24) + (b3 << 16) + (b2 << 8) + b1);
		else
			value = (((long) b1 << 24) + (b2 << 16) + (b3 << 8) + b4);// 1001
		return "" + value;
	}

	public String readOctetString(int len) throws Exception {
		require(len);
		byte[] out = new byte[len];
		System.arraycopy(array, pos, out, 0, len);
		pos += len;
		return new String(out);
	}

	// 传入字符集
	public String readOctetString(int len, String local) throws Exception {// 21
																			// gbk
		require(len);
		byte[] out = new byte[len];
		System.arraycopy(array, pos, out, 0, len);
		// System.out.println("out : "+Arrays.toString(out));

		pos += len;
		return new String(out, local);
	}

	public String readCOctetString(int len) throws Exception {
		require(len);
		byte[] out = new byte[len];
		System.arraycopy(array, pos, out, 0, len);
		pos += len;
		// System.out.println(Arrays.toString(out));
		// System.out.println(Arrays.toString(out));
		return (new String(out));
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
}