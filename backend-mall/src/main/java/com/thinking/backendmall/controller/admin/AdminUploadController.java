package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.Result;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminUploadController {
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.upload-url-prefix:/uploads}")
    private String uploadUrlPrefix;

    @PostMapping("/upload")
    @PreAuthorize("@permissionService.hasPerm('admin:upload')")
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

            Path directory = Paths.get(uploadDir);
            Files.createDirectories(directory);
            Path target = directory.resolve(fileName);
            file.transferTo(target.toFile());

            String urlPrefix = uploadUrlPrefix.endsWith("/")
                    ? uploadUrlPrefix.substring(0, uploadUrlPrefix.length() - 1)
                    : uploadUrlPrefix;
            String url = urlPrefix + "/" + fileName;

            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("fileName", fileName);
            return Result.success(result);
        } catch (Exception ex) {
            return Result.error(500, "Upload failed");
        }
    }
}
