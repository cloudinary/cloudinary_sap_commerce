package uk.ptr.cloudinary.service;

import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface AnalyticsApiService {

    public ResponseEntity<String> activateCloudinaryConnectionWithSAP();
    public ResponseEntity<String> deactivateCloudinaryConnectionWithSAP();
}
