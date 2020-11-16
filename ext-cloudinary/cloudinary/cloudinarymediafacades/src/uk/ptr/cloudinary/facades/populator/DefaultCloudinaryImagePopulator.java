package uk.ptr.cloudinary.facades.populator;

import de.hybris.platform.commercefacades.product.converters.populator.ImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import uk.ptr.cloudinary.service.SyncMediaCloudinaryStrategy;

import javax.annotation.Resource;

public class DefaultCloudinaryImagePopulator extends ImagePopulator implements Populator<MediaModel, ImageData> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCloudinaryImagePopulator.class);

    @Resource
    SyncMediaCloudinaryStrategy syncMediaCloudinaryStrategy;

    @Override
    public void populate(final MediaModel source, final ImageData target) {
        {
            Assert.notNull(source, "Parameter source cannot be null.");
            Assert.notNull(target, "Parameter target cannot be null.");

            if (source.getCloudinaryURL() == null) {
                try {
                    LOG.info("Calling on demand sync for Image" , source.getCode());
                    MediaModel media  =  syncMediaCloudinaryStrategy.onDemandSyncMedia(source);
                    populateData(media, target);
                } catch (Exception e) {
                    LOG.error("Error on uploading image to cloudinary ", e);
                }
            }
            else {
                populateData(source, target);
            }
        }
    }
        private void populateData(MediaModel source, ImageData target) {
        target.setUrl(source.getURL());
        target.setAltText(source.getAltText());
        if (source.getMediaFormat() != null)
        {
            target.setFormat(source.getMediaFormat().getQualifier());
        }
    }
}
