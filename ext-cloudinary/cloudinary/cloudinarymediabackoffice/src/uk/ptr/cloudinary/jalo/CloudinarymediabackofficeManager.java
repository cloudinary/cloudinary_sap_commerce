/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved
 */
package uk.ptr.cloudinary.jalo;

import uk.ptr.cloudinary.constants.CloudinarymediabackofficeConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

public class CloudinarymediabackofficeManager extends GeneratedCloudinarymediabackofficeManager
{
	public static final CloudinarymediabackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (CloudinarymediabackofficeManager) em.getExtension(CloudinarymediabackofficeConstants.EXTENSIONNAME);
	}
	
}
