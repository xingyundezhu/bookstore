package com.bookstore.service.impl;

import com.bookstore.config.jwt.JwtTokenUtil;
import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.LoginResponse;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.entity.User;
import com.bookstore.mapper.UserMapper;
import com.bookstore.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    private final Map<String, String> emailCodes = new ConcurrentHashMap<>();
    private final Map<String, String> phoneCodes = new ConcurrentHashMap<>();
    private final Map<String, Long> codeExpireTime = new ConcurrentHashMap<>();
    private static final long CODE_EXPIRE_MS = 5 * 60 * 1000;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNickname(request.getUsername());
        user.setStatus(1);
        user.setRole(User.ROLE_USER);
        userMapper.insert(user);
        return user;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw new RuntimeException("账户已被禁用");
        }
        String token = jwtTokenUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        return response;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);
        if (user.getNickname() != null) existingUser.setNickname(user.getNickname());
        if (user.getPhone() != null) existingUser.setPhone(user.getPhone());
        if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
        if (user.getGender() != null) existingUser.setGender(user.getGender());
        if (user.getBirthday() != null) existingUser.setBirthday(user.getBirthday());
        if (user.getBio() != null) existingUser.setBio(user.getBio());
        userMapper.update(existingUser);
        return existingUser;
    }

    @Override
    @Transactional
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = getUserById(id);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        userMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public void updateAvatar(Long id, String avatar) {
        userMapper.updateAvatar(id, avatar);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        List<User> users = userMapper.findAll();
        return new PageImpl<>(users, pageable, users.size());
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }

    @Override
    public void sendEmailCode(String email) {
        emailCodes.put(email, generateCode());
        codeExpireTime.put(email, System.currentTimeMillis() + CODE_EXPIRE_MS);
    }

    @Override
    public void sendPhoneCode(String phone) {
        phoneCodes.put(phone, generateCode());
        codeExpireTime.put(phone, System.currentTimeMillis() + CODE_EXPIRE_MS);
    }

    @Override
    @Transactional
    public User bindEmail(Long userId, String email, String code) {
        String savedCode = emailCodes.get(email);
        Long expireTime = codeExpireTime.get(email);
        if (savedCode == null || !savedCode.equals(code)) throw new RuntimeException("验证码错误");
        if (expireTime == null || System.currentTimeMillis() > expireTime) throw new RuntimeException("验证码已过期");
        User existingUser = userMapper.findByEmail(email);
        if (existingUser != null && !existingUser.getId().equals(userId)) throw new RuntimeException("该邮箱已被其他用户绑定");
        User user = getUserById(userId);
        user.setEmail(email);
        userMapper.update(user);
        emailCodes.remove(email);
        codeExpireTime.remove(email);
        return user;
    }

    @Override
    @Transactional
    public User bindPhone(Long userId, String phone, String code) {
        String savedCode = phoneCodes.get(phone);
        Long expireTime = codeExpireTime.get(phone);
        if (savedCode == null || !savedCode.equals(code)) throw new RuntimeException("验证码错误");
        if (expireTime == null || System.currentTimeMillis() > expireTime) throw new RuntimeException("验证码已过期");
        User existingUser = userMapper.findByPhone(phone);
        if (existingUser != null && !existingUser.getId().equals(userId)) throw new RuntimeException("该手机号已被其他用户绑定");
        User user = getUserById(userId);
        user.setPhone(phone);
        userMapper.update(user);
        phoneCodes.remove(phone);
        codeExpireTime.remove(phone);
        return user;
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) code.append(random.nextInt(10));
        return code.toString();
    }
}
