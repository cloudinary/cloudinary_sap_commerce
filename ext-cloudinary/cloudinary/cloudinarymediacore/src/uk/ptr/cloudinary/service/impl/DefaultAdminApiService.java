/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import uk.ptr.cloudinary.service.AdminApiService;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DefaultAdminApiService implements AdminApiService
{
    private final String  dateFormat = "dd-MM-yyyy";
    @Resource
    private Cloudinary cloudinary;

    @Override
    public ApiResponse getDataUsagesInformation() throws Exception {

        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

        return cloudinary.api().usage(ObjectUtils.asMap("date", date.format(formatter)));

    }
}
