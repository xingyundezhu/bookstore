package com.bookstore.config;

import com.bookstore.mapper.UserMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserMapper userMapper;

    public DataInitializer(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String defaultPassword = "123456";
        String hash = encoder.encode(defaultPassword);
        userMapper.findAll().forEach(user -> userMapper.updatePassword(user.getId(), hash));
    }
}
