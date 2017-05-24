package module.util;

public class NullParamException extends RuntimeException {
	private static final long serialVersionUID = -7561816536040419773L;

	public NullParamException() {
		super();
	}

	public NullParamException(Throwable t) {
		super(t);
	}

	public NullParamException(String message) {
		super(message);
	}

	public NullParamException(String message, Throwable t) {
		super(message, t);
	}
}
