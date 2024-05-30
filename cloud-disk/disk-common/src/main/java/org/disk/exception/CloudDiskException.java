package org.disk.exception;

/**
 * 业务异常
 */
public class CloudDiskException extends RuntimeException {

    public CloudDiskException() {
    }

    public CloudDiskException(String msg) {
        super(msg);
    }

}
