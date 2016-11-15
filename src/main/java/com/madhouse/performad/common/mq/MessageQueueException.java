package com.madhouse.performad.common.mq;

/**
 * Created by Madhouse on 2015/9/10.
 */
public class MessageQueueException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MessageQueueException() {
    }

    public MessageQueueException(String message) {
        super(message);
    }

    public MessageQueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageQueueException(Throwable cause) {
        super(cause);
    }

    public MessageQueueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
