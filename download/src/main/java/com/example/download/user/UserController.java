package com.example.download.user;

import com.example.download.email.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class UserController {   // 类名大写开头

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, @RequestParam String verificationCode, HttpSession session, Model model) {
        // 1. 校验验证码
        String key = "verifyCode_" + user.getEmail();
        String savedCode = (String) session.getAttribute(key);
        Long expiry = (Long) session.getAttribute("verifyCodeExpiry_" + user.getEmail());
        if (savedCode == null || expiry == null || System.currentTimeMillis() > expiry) {
            model.addAttribute("error", "验证码已失效，请重新获取");
            return "register";
        }
        if (!savedCode.equals(verificationCode)) {
            model.addAttribute("error", "验证码错误");
            return "register";
        }
        // 2. 校验用户名邮箱是否重复
        if (userRepository.existsByUserName(user.getUserName())) {
            model.addAttribute("error", "用户名已存在");
            return "register";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "邮箱已被注册");
            return "register";
        }
        // 3. 加密保存用户，直接启用账号（因为验证码已验证）
        String encodedPwd = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encodedPwd);
        user.setEnabled(true); // 验证码通过，直接启用
        userRepository.save(user);
        // 4. 清除 session 中的验证码
        session.removeAttribute(key);
        session.removeAttribute("verifyCodeExpiry_" + user.getEmail());
        return "redirect:/login?registered";
    }

    @PostMapping("/sendCode")
    @ResponseBody
    public Map<String, Object> sendVerificationCode(@RequestParam String email, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        // 2. 频率限制（60秒内不能重复发送）
        String lastKey = "lastSendTime_" + email;
        Long lastSend = (Long) session.getAttribute(lastKey);
        if (lastSend != null && System.currentTimeMillis() - lastSend < 60 * 1000) {
            result.put("success", false);
            result.put("message", "操作太频繁，请稍后再试");
            return result;
        }
        session.setAttribute(lastKey, System.currentTimeMillis());

        // 3. 生成验证码（带大小写字母+数字）
        String code = generateRandomCode(6);
        session.setAttribute("verifyCode_" + email, code);
        session.setAttribute("verifyCodeExpiry_" + email, System.currentTimeMillis() + 10 * 60 * 1000);
        System.out.println("【调试】验证码：" + code);

        // 4. 发送邮件
        try {
            emailService.sendVerificationCode(email, code);
            result.put("success", true);
            result.put("message", "验证码已发送，请查收邮件");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "邮件发送失败：" + e.getMessage());
        }
        return result;
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789"; // 去掉易混淆字符 0,1,I,O,l
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String userName,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        // 1. 根据用户名查询用户
        User user = userRepository.findByUserName(userName);

        if (user == null) {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
        // 2. 检查邮箱是否已验证
        if (!user.isEnabled()) {
            model.addAttribute("error", "邮箱未验证，请先验证邮箱");
            return "login";
        }
        // 3. 使用 BCrypt 验证密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
        // 4. 登录成功，保存用户信息到 session
        session.setAttribute("loginUser", user);
        return "redirect:/home";   // 成功后跳转到主页，你可以先创建一个简单的 /home 页面
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }
        return "home";
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "OK";
    }
}