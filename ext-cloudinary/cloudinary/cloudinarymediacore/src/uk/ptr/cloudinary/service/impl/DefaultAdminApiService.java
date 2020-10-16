/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.jalo.CloudinaryConfig;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.AdminApiService;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DefaultAdminApiService implements AdminApiService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAdminApiService.class);

    @Override
    public ApiResponse getCloudinaryPlanInfo(CloudinaryConfigModel cloudinaryConfigModel) throws IllegalArgumentException, Exception{


            Cloudinary cloudinary = new Cloudinary(cloudinaryConfigModel.getCloudinaryURL());
            LocalDate date = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CloudinarymediacoreConstants.DATE_FORMAT);

            cloudinaryConfigModel.getCloudinaryURL();
            return cloudinary.api().usage(ObjectUtils.asMap("date", date.format(formatter)));


    }
}