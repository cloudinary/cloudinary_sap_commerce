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
    private SyncMediaCloudinaryStrategy syncMediaCloudinaryStrategy;

    @Override
    public void populate(final MediaModel source, final ImageData target) {

            Assert.notNull(source, "Parameter source cannot be null.");
            Assert.notNull(target, "Parameter target cannot be null.");

            if (source.getCloudinaryURL() == null) {
                try {
                    LOG.info("Calling on demand sync for Image" , source.getCode());
                    MediaModel media  =  syncMediaCloudinaryStrategy.onDemandSyncMedia(source);
                    super.populate(media, target);
                    populateCloudinaryData(media, target);
                } catch (Exception e) {
                    LOG.error("Error on uploading image to cloudinary ", e);
                }
            }
            else {
                super.populate(source, target);
                populateCloudinaryData(source, target);
            }
    }
        private void populateCloudinaryData(MediaModel source, ImageData target) {

            if(source.getCloudinaryVersion()!=null){
                target.setCloudinaryVersion(source.getCloudinaryVersion());
            }
            if(source.getCloudinaryPublicId()!=null){
                target.setCloudinaryPublicId(source.getCloudinaryPublicId());
            }
            if(source.getCloudinaryResourceType()!=null){
                target.setCloudinaryResourceType(source.getCloudinaryResourceType());
            }
            if(source.getCloudinaryType()!=null){
                target.setCloudinaryType(source.getCloudinaryType());
            }
            if(source.getCloudinaryMediaFormat()!=null){
                target.setCloudinaryMediaFormat(source.getCloudinaryMediaFormat());
            }
            if(source.getCloudinaryTransformation()!=null){
                target.setCloudinaryTransformation(source.getCloudinaryTransformation());
            }

            if(source.getIsCloudinaryOverride()!=null){
                target.setCloudinaryOverride(source.getIsCloudinaryOverride());
            }

            if(source.getCloudinaryURL()!=null){
                target.setCloudinaryURL(source.getCloudinaryURL());
            }
    }
}
