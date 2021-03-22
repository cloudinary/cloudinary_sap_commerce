package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.CloudinaryConfigService;


public class DefaultCloudinaryConfigService implements CloudinaryConfigService
{

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Override
    public CloudinaryConfigModel getCloudinaryConfigModel() {

         return cloudinaryConfigDao.getCloudinaryConfigModel();
    }
}
