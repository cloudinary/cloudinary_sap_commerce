package uk.ptr.cloudinary.handlers;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.TransformationApiService;

import javax.annotation.Resource;


public class CloudinaryUrlAttributeHandler extends AbstractDynamicAttributeHandler<String,MediaModel>
{
    @Resource
    private TransformationApiService transformationApiService;

    public String get(MediaModel media) {
        return transformationApiService.createTransformation(media,media.getMediaFormat());
    }
}
