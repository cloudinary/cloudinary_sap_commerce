package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.ptr.cloudinary.service.AnalyticsApiService;

import javax.annotation.Resource;
import java.io.IOException;

public class DefaultAnalyticsApiService implements AnalyticsApiService {

    private static String USER_AGENT ="user-agent";
    private static String CLOUDINARY_USER_AGENT ="cloudinary.user.agent";
    private static String ACTIVATED_URL="cloudinary.sap.activated.url";
    private static String DEACTIVATED_URL="cloudinary.sap.deactivated.url";

    @Resource
    private ConfigurationService configurationService;

    @Override
    public ResponseEntity<String> activateCloudinaryConnectionWithSAP() {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_AGENT, configurationService.getConfiguration().getString(CLOUDINARY_USER_AGENT));

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        return restTemplate.exchange(configurationService.getConfiguration().getString(ACTIVATED_URL), HttpMethod.GET, entity, String.class);
    }

    @Override
    public ResponseEntity<String> deactivateCloudinaryConnectionWithSAP() {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_AGENT, configurationService.getConfiguration().getString(CLOUDINARY_USER_AGENT));

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        return restTemplate.exchange(configurationService.getConfiguration().getString(DEACTIVATED_URL), HttpMethod.GET, entity, String.class);

    }
}
