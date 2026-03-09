package com.itheima.classroomsigninbackend.controller;

import com.itheima.classroomsigninbackend.common.Result;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/file")
public class FileController {
    private static final String UPLOADS_DIR = "D:/classroom-signin/uploads/";

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request)
        throws IOException {
        if (file.isEmpty()) {
            return Result.fail(500, "File is empty");
        }
        File dir = new File(UPLOADS_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            return Result.fail(500, "Cannot create upload directory");
        }
        String originalFilename = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(originalFilename);
        String filename = UUID.randomUUID().toString().replace("-", "");
        if (ext != null && !ext.isBlank()) {
            filename = filename + "." + ext;
        }
        File target = new File(dir, filename);
        file.transferTo(target);
        String baseUrl = request.getScheme() + "://" + request.getServerName()
            + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
        String url = baseUrl + "/uploads/" + filename;
        return Result.success(url);
    }
}
