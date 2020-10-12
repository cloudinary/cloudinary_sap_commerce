/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import uk.ptr.cloudinary.constants.CloudinarymediasmarteditConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class CloudinarymediasmarteditManager extends GeneratedCloudinarymediasmarteditManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( CloudinarymediasmarteditManager.class.getName() );
	
	public static final CloudinarymediasmarteditManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (CloudinarymediasmarteditManager) em.getExtension(CloudinarymediasmarteditConstants.EXTENSIONNAME);
	}
	
}
