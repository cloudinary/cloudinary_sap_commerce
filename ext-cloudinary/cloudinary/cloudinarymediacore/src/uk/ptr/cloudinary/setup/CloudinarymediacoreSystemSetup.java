/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.setup;

import static uk.ptr.cloudinary.constants.CloudinarymediacoreConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.service.CloudinarymediacoreService;


@SystemSetup(extension = CloudinarymediacoreConstants.EXTENSIONNAME)
public class CloudinarymediacoreSystemSetup
{
	private final CloudinarymediacoreService cloudinarymediacoreService;

	public CloudinarymediacoreSystemSetup(final CloudinarymediacoreService cloudinarymediacoreService)
	{
		this.cloudinarymediacoreService = cloudinarymediacoreService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		cloudinarymediacoreService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return CloudinarymediacoreSystemSetup.class.getResourceAsStream("/cloudinarymediacore/sap-hybris-platform.png");
	}
}
