package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {
    private static final Path UPLOAD_ROOT = Path.of("uploads");

    @PostMapping({
            "/customers/avatar/upload",
            "/customers/upload-avatar",
            "/files/upload",
            "/upload"
    })
    public ApiResponse<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                         @RequestParam(required = false) String bizType) throws IOException {
        return ApiResponse.success(saveFile(file, bizType == null ? "customer-avatar" : bizType));
    }

    @PostMapping("/shop/logo/upload")
    public ApiResponse<Map<String, Object>> uploadShopLogo(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> data = saveFile(file, "shop-logo");
        data.put("logoUrl", data.get("url"));
        return ApiResponse.success(data);
    }

    private Map<String, Object> saveFile(MultipartFile file, String bizType) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("file is required");
        }
        String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) {
            ext = originalName.substring(dot);
        }
        String safeBizType = bizType.replaceAll("[^a-zA-Z0-9_-]", "-");
        Path dir = UPLOAD_ROOT.resolve(safeBizType);
        Files.createDirectories(dir);
        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        String path = "/uploads/" + safeBizType + "/" + filename;
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("url", path);
        data.put("avatarUrl", path);
        data.put("fileUrl", path);
        data.put("path", path);
        return data;
    }
}
