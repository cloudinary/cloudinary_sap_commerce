/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.setup;

import static uk.ptr.cloudinary.constants.CloudinarymediacoreConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.service.CloudinarymediacoreService;


@SystemSetup(extension = CloudinarymediacoreConstants.EXTENSIONNAME)
public class CloudinarymediacoreSystemSetup extends AbstractSystemSetup
{
	private final CloudinarymediacoreService cloudinarymediacoreService;

	public CloudinarymediacoreSystemSetup(final CloudinarymediacoreService cloudinarymediacoreService)
	{
		this.cloudinarymediacoreService = cloudinarymediacoreService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData(final SystemSetupContext context)
	{
		cloudinarymediacoreService.createLogo(PLATFORM_LOGO_CODE);
		importImpexFile(context, "/cloudinarymediacore/impex/cloudinaryconfig.impex", true);
		importImpexFile(context, "/cloudinarymediacore/impex/cloudinarysynccronjob.impex", true);
		importImpexFile(context, "/cloudinarymediacore/impex/cloudinarybackoffice-users.impex", true);

	}

	@SystemSetup(process = SystemSetup.Process.UPDATE, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialDataForUpdate(final SystemSetupContext context)
	{
		cloudinarymediacoreService.createLogo(PLATFORM_LOGO_CODE);
		importImpexFile(context, "/cloudinarymediacore/impex/cloudinaryconfig.impex", true);
		importImpexFile(context, "/cloudinarymediacore/impex/cloudinarysynccronjob.impex", true);
		importImpexFile(context, "/cloudinarymediacore/impex/cloudinarybackoffice-users.impex", true);

	}

	private InputStream getImageStream()
	{
		return CloudinarymediacoreSystemSetup.class.getResourceAsStream("/cloudinarymediacore/sap-hybris-platform.png");
	}

	@Override
	public List<SystemSetupParameter> getInitializationOptions() {
		return Collections.emptyList();
	}
}
