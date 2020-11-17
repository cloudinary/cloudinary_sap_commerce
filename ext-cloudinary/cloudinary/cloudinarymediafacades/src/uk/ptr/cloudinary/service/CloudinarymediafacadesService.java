/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.service;

public interface CloudinarymediafacadesService
{
	String getHybrisLogoUrl(String logoCode);

	void createLogo(String logoCode);
}
