package com.washhelper.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CaptchaService {

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int WIDTH = 130;
    private static final int HEIGHT = 44;
    private static final int CODE_LEN = 4;
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final String KEY_PREFIX = "washhelper:captcha:";

    private final SecureRandom random = new SecureRandom();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void warmup() {
        try { new Font(Font.SANS_SERIF, Font.BOLD, 28).getNumGlyphs(); } catch (Throwable ignore) {}
    }

    public Map<String, Object> generate() {
        String code = randomCode();
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(KEY_PREFIX + token, code.toLowerCase(), TTL);

        String image;
        try {
            image = renderImage(code);
        } catch (IOException e) {
            throw new IllegalStateException("生成验证码图片失败", e);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", token);
        data.put("image", image);
        data.put("expiresIn", TTL.getSeconds());
        return data;
    }

    public boolean verifyAndConsume(String token, String input) {
        if (token == null || token.isBlank() || input == null || input.isBlank()) return false;
        String key = KEY_PREFIX + token;
        String expected = redisTemplate.opsForValue().get(key);
        if (expected == null) return false;
        redisTemplate.delete(key);
        return expected.equalsIgnoreCase(input.trim());
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LEN);
        for (int i = 0; i < CODE_LEN; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private String renderImage(String code) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        GradientPaint bg = new GradientPaint(0, 0, new Color(255, 247, 235), WIDTH, HEIGHT, new Color(247, 250, 255));
        g.setPaint(bg);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        for (int i = 0; i < 28; i++) {
            g.setColor(new Color(180 + random.nextInt(60), 180 + random.nextInt(60), 200 + random.nextInt(50), 160));
            int r = 2 + random.nextInt(3);
            g.fillOval(random.nextInt(WIDTH), random.nextInt(HEIGHT), r, r);
        }

        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(255, 143, 66, 130 + random.nextInt(80)));
            g.setStroke(new BasicStroke(1.2f));
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT), random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }

        Color[] palette = {
                new Color(255, 105, 60),
                new Color(60, 110, 230),
                new Color(40, 160, 110),
                new Color(180, 90, 200)
        };
        int charWidth = (WIDTH - 24) / CODE_LEN;
        for (int i = 0; i < code.length(); i++) {
            int fontSize = 26 + random.nextInt(4);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
            g.setColor(palette[random.nextInt(palette.length)]);
            double angle = (random.nextDouble() - 0.5) * 0.6;
            int x = 14 + i * charWidth;
            int y = HEIGHT / 2 + fontSize / 3;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.rotate(angle, x, y);
            g2.drawString(String.valueOf(code.charAt(i)), x, y);
            g2.dispose();
        }

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
