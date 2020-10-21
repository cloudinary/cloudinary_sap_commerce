/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package uk.ptr.cloudinary.service;

import de.hybris.platform.core.model.media.MediaModel;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.response.UploadApiResponseData;

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

    /**
     * Upload API
     *
     * @param cloudinaryConfigModel
     * @param mediaModel
     * @param tag
     * @return
     * @throws IllegalArgumentException
     * @throws Exception
     */
     UploadApiResponseData uploadAsset(CloudinaryConfigModel cloudinaryConfigModel, MediaModel mediaModel, String tag) throws IllegalArgumentException, Exception;

    }
