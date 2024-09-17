/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * The files in this addon are licensed under the Apache Software License, v. 2
 * except as noted otherwise in the LICENSE file.
 */
package de.hybris.platform.spartacussampledata.setup.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.addonsupport.setup.impl.DefaultAddonSampleDataImportService;
import de.hybris.platform.catalog.jalo.SyncItemCronJob;
import de.hybris.platform.catalog.jalo.SyncItemJob;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * This class extends {@link DefaultAddonSampleDataImportService} and specifies how to import sample data spartacus
 */
@SuppressWarnings("deprecation")
public class SpaSampleDataImportService extends DefaultAddonSampleDataImportService
{
	private static final String SYNC_CONTENT_CATALOG = "electronics->spa";
	private static final String CONTENT_CATALOGS = "/contentCatalogs/";

	private ModelService modelService;

	@Override
	protected void importContentCatalog(final SystemSetupContext context, final String importRoot, final String catalogName)
	{
		final boolean catalogNameOk = "electronics".equals(catalogName) || "apparel-uk".equals(catalogName)  || "powertools".equals(catalogName);
		if (catalogNameOk)
		{
			// 1- create new catalog
			importImpexFile(context, importRoot + CONTENT_CATALOGS + catalogName + "ContentCatalog/catalog.impex", false);

			// 2- sync xxxContentCatalog:Staged->xxx-spaContentCatalog:Staged
			final CatalogVersionModel catalog = getCatalogVersionService().getCatalogVersion(catalogName + "-spaContentCatalog", "Staged");
			final List<SyncItemJobModel> synItemsJobs = catalog.getIncomingSynchronizations();
			if (synItemsJobs.size() > 0)
			{
				final SyncItemJobModel job = synItemsJobs.get(0);
				final SyncItemJob jobItem = getModelService().getSource(job);
				synchronizeSpaContentCatalog(context, jobItem);
			}

			// 3- perform some cleaning
			importImpexFile(context, importRoot + CONTENT_CATALOGS + catalogName + "ContentCatalog/cleaning.impex", false);

			// 4- solr ammendments
			importImpexFile(context, importRoot + "/productCatalogs/" + catalogName + "ProductCatalog/solr.impex", false);
		}

		// 4- import content catalog from impex
		super.importContentCatalog(context, importRoot, catalogName);

		if (catalogNameOk)
		{
			// 5- synchronize spaContentCatalog:staged->online
			synchronizeContentCatalog(context, catalogName + "-spa", true);

			// 6- give permission to cmsmanager to do the sync
			importImpexFile(context, importRoot + CONTENT_CATALOGS + catalogName + "ContentCatalog/sync.impex", false);

			// 7- import email data
			importImpexFile(context, importRoot + CONTENT_CATALOGS + catalogName + "ContentCatalog/email-content.impex", false);
		}
	}


	@Override
	protected void importStoreLocations(final SystemSetupContext context, final String importRoot, final String storeName)
	{
		super.importStoreLocations(context, importRoot, storeName);
	}


	private void synchronizeSpaContentCatalog(final SystemSetupContext context, final SyncItemJob syncJobItem)
	{
		logInfo(context, "Begin synchronizing Content Catalog [" + SYNC_CONTENT_CATALOG + "] - synchronizing");

		final SyncItemCronJob syncCronJob = syncJobItem.newExecution();
		syncCronJob.setLogToDatabase(false);
		syncCronJob.setLogToFile(false);
		syncCronJob.setForceUpdate(false);
		syncJobItem.configureFullVersionSync(syncCronJob);

		logInfo(context, "Starting synchronization, this may take a while ...");
		syncJobItem.perform(syncCronJob, true);

		logInfo(context, "Synchronization complete for catalog [" + SYNC_CONTENT_CATALOG + "]");
		final CronJobResult result = modelService.get(syncCronJob.getResult());
		final CronJobStatus status = modelService.get(syncCronJob.getStatus());

		final PerformResult syncCronJobResult = new PerformResult(result, status);
		if (isSyncRerunNeeded(syncCronJobResult))
		{
			logInfo(context, "Catalog catalog [" + SYNC_CONTENT_CATALOG + "] sync has issues.");
		}

		logInfo(context, "Done synchronizing  Content Catalog [" + SYNC_CONTENT_CATALOG + "]");
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
