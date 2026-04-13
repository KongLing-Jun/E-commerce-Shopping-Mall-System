package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminUploadController {
    private static final Logger log = LoggerFactory.getLogger(AdminUploadController.class);
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Value("${app.upload-dir:upload}")
    private String uploadDir;

    @Value("${app.upload-url-prefix:/upload}")
    private String uploadUrlPrefix;

    @PostMapping("/upload")
    @PreAuthorize("@permissionService.hasPerm('admin:upload')")
    // 功能：上传数据
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error(400, "File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error(400, "File is too large");
        }
        if (file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            return Result.error(400, "Invalid file type");
        }
        try {
            String originalName = file.getOriginalFilename();
            String ext = StringUtils.getFilenameExtension(originalName);
            if (ext == null || ext.isBlank() || !ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
                return Result.error(400, "Invalid file extension");
            }
            String fileName = UUID.randomUUID().toString().replace("-", "");
            fileName = fileName + "." + ext.toLowerCase();

            Path directory = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            Path target = directory.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String urlPrefix = uploadUrlPrefix.endsWith("/")
                    ? uploadUrlPrefix.substring(0, uploadUrlPrefix.length() - 1)
                    : uploadUrlPrefix;
            String url = urlPrefix + "/" + fileName;

            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("fileName", fileName);
            return Result.success(result);
        } catch (Exception ex) {
            log.error("Upload failed: fileName={}, contentType={}, size={}",
                    file == null ? null : file.getOriginalFilename(),
                    file == null ? null : file.getContentType(),
                    file == null ? null : file.getSize(),
                    ex);
            return Result.error(500, "Upload failed");
        }
    }
}
