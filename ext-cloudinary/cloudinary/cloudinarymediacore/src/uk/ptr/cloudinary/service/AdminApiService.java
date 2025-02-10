/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package uk.ptr.cloudinary.service;

import com.cloudinary.api.ApiResponse;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;

public interface AdminApiService {

    public ApiResponse getCloudinaryPlanInfo(String cloudinaryURL) throws IllegalArgumentException, Exception;
}
