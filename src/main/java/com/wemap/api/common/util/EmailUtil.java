package com.wemap.api.common.util;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final JavaMailSender emailSender;

    public String createCode() {
        StringBuilder code = new StringBuilder();
        Random randomNum = new Random();

        for (int i = 0; i < 8; i++) {
            // 0~2 랜덤
            int index = randomNum.nextInt(3);
            switch (index) {
                case 0:
                    // a~Z (ex. 1+97=98 => (char)98 = 'b'
                    code.append((char)(randomNum.nextInt(26) + 97));
                    break;
                case 1:
                    // A~Z
                    code.append((char)(randomNum.nextInt(26) + 65));
                    break;;
                case 2:
                    // 0~9
                    code.append(randomNum.nextInt(10));
                    break;
            }
        }
        return code.toString();
    }

    public MimeMessage createMessage(String email, String code) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            // 보내는 대상
            message.addRecipients(Message.RecipientType.TO, email + "@gmail.com");
            // 제목
            message.setSubject("WeMap 회원가입 : 이메일 인증 코드");
            String msg = "<h1>WeMap 회원가입 : 이메일 인증 코드입니다.</h1>" + "<br>" + "<h3>CODE : <strong>" + code + "</strong></div>" + "<br>" + "<h3>감사합니다.<h3>";
            message.setText(msg, "utf-8", "html");//내용
            message.setFrom(new InternetAddress("wemap1995@gmail.com", "WeMap"));//보내는 사람
            return message;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
