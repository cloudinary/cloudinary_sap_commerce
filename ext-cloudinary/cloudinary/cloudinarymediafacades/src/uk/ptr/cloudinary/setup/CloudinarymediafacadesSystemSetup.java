/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.setup;

import static uk.ptr.cloudinary.constants.CloudinarymediafacadesConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import uk.ptr.cloudinary.constants.CloudinarymediafacadesConstants;
import uk.ptr.cloudinary.service.CloudinarymediafacadesService;


@SystemSetup(extension = CloudinarymediafacadesConstants.EXTENSIONNAME)
public class CloudinarymediafacadesSystemSetup
{
	private final CloudinarymediafacadesService cloudinarymediafacadesService;

	public CloudinarymediafacadesSystemSetup(final CloudinarymediafacadesService cloudinarymediafacadesService)
	{
		this.cloudinarymediafacadesService = cloudinarymediafacadesService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		cloudinarymediafacadesService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return CloudinarymediafacadesSystemSetup.class.getResourceAsStream("/cloudinarymediafacades/sap-hybris-platform.png");
	}
}
