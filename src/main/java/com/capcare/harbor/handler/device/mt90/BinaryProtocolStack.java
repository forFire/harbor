package com.capcare.harbor.handler.device.mt90;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jy.zhao
 */
public class BinaryProtocolStack {

	private Logger logger = LoggerFactory.getLogger(BinaryProtocolStack.class);

	/**
	 * 协议缓冲区
	 */
	public byte[] buffer;

	/**
	 * 缓冲区边界
	 */
	public int limit;

	public BinaryProtocolStack() {
		buffer = new byte[4096];

		limit = 0;
	}

	/**
	 * 将content添加到协议栈缓冲区末尾 如果容量超过缓冲区大小，自动扩展
	 */
	public void append(byte[] content) {
		int len = limit + content.length;

		if (len > buffer.length) {
			// 容量每次扩展为原来的2倍
			int capacity = buffer.length + buffer.length;
			while (capacity < len) {
				capacity = capacity + capacity;
			}

			// 拷贝原协议栈缓冲区中的内容到新的缓冲区中
			byte[] tmp = new byte[capacity];
			System.arraycopy(buffer, 0, tmp, 0, limit);
			buffer = tmp;

			logger.info("协议栈缓冲区扩容, 当前容量:" + capacity);
		}

		System.arraycopy(content, 0, buffer, limit, content.length);
		limit += content.length;
	}

	/**
	 * 从协议栈缓冲区中删除指定区间的内容
	 * 
	 * @param start
	 *            删除内容的起始位置（包含）
	 * @param end
	 *            删除内容的结束位置（不包含）
	 */
	public void remove(int start, int end) {
		logger.debug("缓冲区长度：" + limit + ", 起始位置:" + start + ", 结束位置:" + end);

		if (start < 0) {
			throw new IllegalArgumentException("起始位置不能为负值");
		}

		if (end < start) {
			throw new IllegalArgumentException("结束位置必须大于起始位置");
		}

		if (end > limit) {
			throw new IllegalArgumentException("结束位置不能超出缓冲区当前极限");
		}

		if (end == limit) {
			// 只需要更新缓冲区极限位置
			limit = start;
		} else {
			// 将结束位置后的内容拷贝到起始位置
			int len = limit - end;
			System.arraycopy(buffer, end, buffer, start, len);

			// 更新缓冲区的极限位置
			limit = start + len;
		}

		// System.out.println("LEFT:[" + new String(buffer, 0, limit) + "]");
	}

	/**
	 * 从协议栈缓冲区中删除所有的pattern内容
	 */
	public void remove(byte[] pattern, int[] prefix) {
		int pos = indexOf(pattern, prefix, 0);

		if (pos == -1) {
			return;
		}

		int nextPos = indexOf(pattern, prefix, pos + pattern.length);

		int idx = pos;
		while (nextPos != -1) {
			// 移动两个标记之前的部分
			for (int i = pos + pattern.length; i < nextPos;) {
				buffer[idx++] = buffer[i++];
			}

			pos = nextPos;
			nextPos = indexOf(pattern, prefix, pos + pattern.length);
		}

		for (int i = pos + pattern.length; i < limit;) {
			buffer[idx++] = buffer[i++];
		}

		// 更新缓冲区大小
		limit = idx;
	}

	/**
	 * 清空缓冲区
	 */
	public void clear() {
		// 将极限设为0
		limit = 0;
	}

	/**
	 * 使用KMP算法，从指定位置开始，查找pattern在buffer中首次出现的位置
	 */
	public int indexOf(byte[] pattern, int[] prefix, int startFrom) {
		if (limit < (startFrom + pattern.length)) {
			return -1;
		}

		int i = startFrom;
		int q = 0;
		for (; i < limit; i++) {
			while (q > 0 && pattern[q] != buffer[i]) {
				q = prefix[q - 1];
			}

			if (pattern[q] == buffer[i]) {
				q++;
			}

			if (q == pattern.length) {
				return i - q + 1;
			}
		}

		return -1;
	}

	public int indexOf(byte target, int startFrom) {
		if (!(limit > startFrom)) {
			return -1;
		}

		for (int i = startFrom; i < limit; i++) {
			if (buffer[i] == target) {
				return i;
			}
		}

		return -1;
	}

	public byte[] getBytes(int start, int end) {
		byte[] bytes = null;
		if (start < 0) {
			throw new IllegalArgumentException("起始位置不能为负值");
		}

		if (end < start) {
			throw new IllegalArgumentException("结束位置必须大于起始位置");
		}

		if (end > limit) {
			throw new IllegalArgumentException("结束位置不能超出缓冲区当前极限");
		}

		int len = end - start;
		bytes = new byte[len];
		System.arraycopy(buffer, start, bytes, end, len);

		return bytes;
	}

	/**
	 * 计算KMP算法中所需的回溯位置
	 * 
	 * @param pattern
	 * @return
	 */
	public static int[] computePrefix(byte[] pattern) {
		int[] prefix = new int[pattern.length];

		prefix[0] = 0;
		int k = 0;
		for (int i = 1; i < pattern.length; i++) {
			while (k > 0 && pattern[k] != pattern[i]) {
				k = prefix[k - 1];
			}

			if (pattern[k] == pattern[i]) {
				k++;
			}

			prefix[i] = k;
		}

		return prefix;
	}
}
