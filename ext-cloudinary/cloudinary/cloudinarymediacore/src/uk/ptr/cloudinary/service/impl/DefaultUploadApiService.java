package uk.ptr.cloudinary.service.impl;

import java.io.IOException;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import uk.ptr.cloudinary.service.UploadApiService;


/**
 * The type Default upload api service.
 */
public class DefaultUploadApiService implements UploadApiService
{
    @Override
    public Map deleteAsset(String cloudinaryURL, String publicId) throws IOException
    {
        Cloudinary cloudinary = new Cloudinary(cloudinaryURL);

        return cloudinary.uploader().destroy(publicId,ObjectUtils.asMap("invalidate", Boolean.TRUE));
    }
}
