package com.jnana.helper;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryService {
	
	 private final Cloudinary cloudinary;

	    public CloudinaryService(@Value("${cloudinary.api.cloud_name}") String cloudName,
	                             @Value("${cloudinary.api.key}") String apiKey,
	                             @Value("${cloudinary.api.secret}") String apiSecret) {
	        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
	            "cloud_name", cloudName,
	            "api_key", apiKey,
	            "api_secret", apiSecret
	        ));
	    }
	    
	  

}
