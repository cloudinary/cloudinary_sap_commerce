/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.exceptions;

import de.hybris.platform.core.model.c2l.LanguageModel;

import javax.servlet.ServletException;


public class UnsupportedLanguageException extends ServletException
{

	private final LanguageModel language;

	/**
	 * @param languageToSet
	 */
	public UnsupportedLanguageException(final LanguageModel languageToSet)
	{
		super("Language " + languageToSet + " is not supported by the current base store");
		this.language = languageToSet;
	}

	public UnsupportedLanguageException(final LanguageModel languageToSet, final Throwable rootCause)
	{
		super("Language " + languageToSet + " is not supported by the current base store", rootCause);
		this.language = languageToSet;
	}

	/**
	 * @param msg
	 */
	public UnsupportedLanguageException(final String msg)
	{
		super(msg);
		language = null;
	}

	public UnsupportedLanguageException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
		language = null;
	}

	/**
	 * @return the language
	 */
	public LanguageModel getLanguage()
	{
		return language;
	}
}
