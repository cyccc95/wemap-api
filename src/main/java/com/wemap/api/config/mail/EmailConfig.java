package com.wemap.api.config.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${mail.smtp.auth}")
    private boolean auth;
    @Value("${mail.smtp.starttls.required}")
    private boolean starttlsRequired;
    @Value("${mail.smtp.starttls.enable}")
    private boolean starttlsEnable;
    @Value("${mail.smtp.port}")
    private int port;
    @Value("${mail.smtp.socketFactory.port}")
    private int socketFactoryPort;
    @Value("${mail.smtp.socketFactory.class}")
    private String socketFactoryClass;
    @Value("${mail.smtp.socketFactory.fallback}")
    private boolean socketFactoryFallback;
    @Value("${AdminMail.id}")
    private String id;
    @Value("${AdminMail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setUsername(id);
        javaMailSender.setPassword(password);
        javaMailSender.setPort(port);
        javaMailSender.setJavaMailProperties(getMailProperties());
        javaMailSender.setDefaultEncoding("UTF-8");
        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.starttls.required", starttlsRequired);
        properties.put("mail.smtp.starttls.enable", starttlsEnable);
        properties.put("mail.smtp.socketFactory.port", socketFactoryPort);
        properties.put("mail.smtp.socketFactory.class", socketFactoryClass);
        properties.put("mail.smtp.socketFactory.fallback", socketFactoryFallback);
        return properties;
    }
}
