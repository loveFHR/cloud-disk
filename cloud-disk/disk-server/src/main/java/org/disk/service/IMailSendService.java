package org.disk.service;

public interface IMailSendService {
    /**
     * 发送纯文本邮件
     * @param to
     * @param subject
     * @param text
     */
    void sendTextMailMessage(String to,String subject,String text);

}
