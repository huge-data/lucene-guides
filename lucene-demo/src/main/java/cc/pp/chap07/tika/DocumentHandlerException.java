package cc.pp.chap07.tika;

import java.io.PrintStream;
import java.io.PrintWriter;

public class DocumentHandlerException extends Exception {

	private static final long serialVersionUID = 1L;

	private Throwable cause;

	public DocumentHandlerException() {
		super();
	}

	public DocumentHandlerException(String message) {
		super(message);
	}

	public DocumentHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public Throwable getException() {
		return cause;
	}

	@Override
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	@Override
	public void printStackTrace(PrintStream ps) {
		synchronized (ps) {
			super.printStackTrace(ps);
			if (cause != null) {
				ps.println("--- Nested Exception ---");
				cause.printStackTrace(ps);
			}
		}
	}

	@Override
	public void printStackTrace(PrintWriter pw) {
		synchronized (pw) {
			super.printStackTrace(pw);
			if (cause != null) {
				pw.println("--- Nested Exception ---");
				cause.printStackTrace(pw);
			}
		}
	}

}
