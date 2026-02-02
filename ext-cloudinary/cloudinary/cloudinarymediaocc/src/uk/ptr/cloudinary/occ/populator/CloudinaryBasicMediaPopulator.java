package uk.ptr.cloudinary.occ.populator;

import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.media.populator.BasicMediaPopulator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;

public class CloudinaryBasicMediaPopulator extends BasicMediaPopulator
{
    @Override
    public void populate(MediaModel source, MediaData target) throws ConversionException {
        super.populate(source, target);
        if (Objects.nonNull(source.getCloudinaryURL()))
        {
            target.setUrl(source.getCloudinaryURL());
        }
    }
}
