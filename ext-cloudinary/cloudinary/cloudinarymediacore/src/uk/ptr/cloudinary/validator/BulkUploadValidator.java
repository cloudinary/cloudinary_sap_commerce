package uk.ptr.cloudinary.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.ptr.cloudinary.dto.BulkUploadRequestData;
import uk.ptr.cloudinary.dto.CloudinaryProductAssestData;
import uk.ptr.cloudinary.dto.MediaContainerData;

import java.util.Collection;
import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

@Component("bulkUploadValidator")
public class BulkUploadValidator  implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {

        final BulkUploadRequestData bulkUploadRequestData = (BulkUploadRequestData) o;

        final List<CloudinaryProductAssestData> data = bulkUploadRequestData.getProductMediaAssest();

        if (!CollectionUtils.isEmpty(data))
        {
            data.stream().forEach(d -> {
                if(StringUtils.isEmpty(d.getProductCode()))
                {
                    errors.reject("productCode", "Product Code can not be null");
                    return;
                }
                final List<MediaContainerData> mediaContainerData = d.getMediaContainers();
                if (!CollectionUtils.isEmpty(mediaContainerData)) {
                    mediaContainerData.stream().forEach(mc ->{
                        if(StringUtils.isEmpty(mc.getPublicId()))
                        {
                            errors.reject("publicId", "Public Id can not be null");
                        }
                        if(StringUtils.isEmpty(mc.getResourceType()))
                        {
                            errors.reject("resourceType", "Resource Type can not be null");
                        }
                        if(StringUtils.isEmpty(mc.getCloudinaryType()))
                        {
                            errors.reject("cloudinaryType", "Cloudinary Type can not be null");
                        }
                        if(StringUtils.isEmpty(mc.getCloudinaryMediaFormat()))
                        {
                            errors.reject("cloudinaryMediaFormat", "Cloudinary Media Format can not be null");
                        }
                    });
                }
            });
        }
        else {
            errors.reject("Missing Data", "Please provide product and media data");
        }
    }
}
