/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package uk.ptr.cloudinary.service;

import com.cloudinary.api.ApiResponse;

public interface AdminApiService {

    public ApiResponse getDataUsagesInformation() throws Exception;
}
