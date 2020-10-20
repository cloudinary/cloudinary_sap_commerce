/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package uk.ptr.cloudinary.service;

import java.io.IOException;
import java.util.Map;


/**
 * The interface for Upload api service.
 */
public interface UploadApiService
{
     /**
      * Delete asset map.
      *
      * @param cloudinaryURL
      *         the cloudinary url
      * @param publicId
      *         the public id
      *
      * @return the map
      * @throws IOException
      *         the io exception
      */
     Map deleteAsset(String cloudinaryURL, String publicId) throws IOException;
}
