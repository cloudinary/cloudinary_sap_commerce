/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package uk.ptr.cloudinary.service;

import java.util.Map;

public interface PresetApiService {

    Map<String, Boolean> getUploadPresets(String cloudinaryURL) throws IllegalArgumentException, Exception;
}
