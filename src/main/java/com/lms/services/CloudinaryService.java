package com.lms.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lms.utils.Env;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", Env.get("CLOUDINARY_NAME"),
            "api_key", Env.get("CLOUDINARY_API_KEY"),
            "api_secret", Env.get("CLOUDINARY_API_SECRET")
        ));
    }

    public String uploadFile(MultipartFile file) {
        try {
            File tempFile = File.createTempFile("temp", file.getOriginalFilename());
            file.transferTo(tempFile);

            Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");

        } catch (Exception e) {
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }
}
