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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DefaultAdminApiService implements AdminApiService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAdminApiService.class);

    @Override
    public ApiResponse getCloudinaryPlanInfo(String cloudinaryURL) throws IllegalArgumentException, Exception{

            Cloudinary cloudinary = new Cloudinary(cloudinaryURL);
            //cloudinary.setUserAgent(CloudinarymediacoreConstants.CLOUDINARYSAPCC, CloudinarymediacoreConstants.CLOUDINARY_VERSION + "(SAPCC" + CloudinarymediacoreConstants.SAP_VERSION + ")");
            return cloudinary.api().usage(ObjectUtils.emptyMap());
    }
}