package com.example.kitchenstore.email;

import java.io.File;

public class SendMailUtil {
    //qq
    private static final String HOST = "smtp.qq.com";
    private static final String PORT = "587";
    private static final String FROM_ADD = "673498950@qq.com"; //发送方邮箱
    private static final String FROM_PSW = "zopmdtxgwvspbdic";//发送方邮箱授权码
    //imap/smtp  aoegkyvjjkpdbajf
    //pop3 smtp   zqqybepxhrsgbdbe





    public static void send(String toAdd, String title, String content) {
        final MailInfo mailInfo = creatMail(toAdd, title, content);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendTextMail(mailInfo);
            }
        }).start();
    }


    private static MailInfo creatMail(String toAdd, String title, String content) {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(HOST);
        mailInfo.setMailServerPort(PORT);
        mailInfo.setValidate(true);
        mailInfo.setUserName(FROM_ADD); // 你的邮箱地址
        mailInfo.setPassword(FROM_PSW);// 您的邮箱密码
        mailInfo.setFromAddress(FROM_ADD); // 发送的邮箱
        mailInfo.setToAddress(toAdd); // 发到哪个邮件去
        mailInfo.setSubject(title); // 邮件主题
        mailInfo.setContent(content); // 邮件文本
        return mailInfo;
    }
}
