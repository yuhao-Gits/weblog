package com.yuhao.weblog.module.comment.service.impl;

import com.yuhao.weblog.module.admin.mapper.BlogSettingsMapper;
import com.yuhao.weblog.module.admin.model.entity.BlogSettings;
import com.yuhao.weblog.module.comment.model.entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 评论邮件通知：评论发布后异步通知博主。
 * 未配置邮箱或发送失败时静默跳过，不影响评论主流程。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentMailNotifier {

    private final BlogSettingsMapper blogSettingsMapper;

    @Resource(name = "asyncTaskExecutor")
    private Executor asyncTaskExecutor;

    public void notifyAsync(Comment comment) {
        asyncTaskExecutor.execute(() -> {
            try {
                send(comment);
            } catch (Exception e) {
                log.warn("评论邮件通知失败（可忽略）, msg={}", e.getMessage());
            }
        });
    }

    private void send(Comment comment) throws Exception {
        BlogSettings settings = blogSettingsMapper.selectById(1L);
        if (settings == null || StringUtils.isBlank(settings.getEmailHost())
                || StringUtils.isBlank(settings.getEmailUsername())
                || StringUtils.isBlank(settings.getEmailPassword())) {
            return; // 未配置邮箱，跳过
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", settings.getEmailHost());
        props.put("mail.smtp.port", settings.getEmailPort() == null ? "465" : String.valueOf(settings.getEmailPort()));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");

        Session session = Session.getInstance(props);
        MimeMessage msg = new MimeMessage(session);
        String from = StringUtils.isNotBlank(settings.getEmailFrom())
                ? settings.getEmailFrom() : settings.getEmailUsername();
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(settings.getEmailUsername()));
        msg.setSubject("【博客评论通知】" + comment.getNickname() + " 发表了评论");
        msg.setText("评论人：" + comment.getNickname() + "\n\n内容：\n" + comment.getContent(), "UTF-8");

        Transport.send(msg);
        log.info("评论邮件通知已发送");
    }
}
