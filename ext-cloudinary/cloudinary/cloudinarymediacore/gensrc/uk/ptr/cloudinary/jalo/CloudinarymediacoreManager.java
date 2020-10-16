/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 16 Oct 2020, 15:34:50                       ---
 * ----------------------------------------------------------------
 */
package uk.ptr.cloudinary.jalo;

import de.hybris.platform.directpersistence.annotation.SLDSafe;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.jalo.media.AbstractMedia;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.jalo.CloudinaryConfig;

/**
 * Generated class for type <code>CloudinarymediacoreManager</code>.
 */
@SuppressWarnings({"unused","cast"})
@SLDSafe
public class CloudinarymediacoreManager extends Extension
{
	protected static final Map<String, Map<String, AttributeMode>> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, Map<String, AttributeMode>> ttmp = new HashMap();
		Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put("cloudinaryURL", AttributeMode.INITIAL);
		tmp.put("cloudinaryResourceType", AttributeMode.INITIAL);
		tmp.put("cloudinaryType", AttributeMode.INITIAL);
		tmp.put("cloudinaryTransformation", AttributeMode.INITIAL);
		tmp.put("cloudinaryPublicId", AttributeMode.INITIAL);
		tmp.put("cloudinaryFetchURL", AttributeMode.INITIAL);
		tmp.put("isCloudinaryFetch", AttributeMode.INITIAL);
		tmp.put("isCloudinaryOverride", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.jalo.media.Media", Collections.unmodifiableMap(tmp));
		DEFAULT_INITIAL_ATTRIBUTES = ttmp;
	}
	@Override
	public Map<String, AttributeMode> getDefaultAttributeModes(final Class<? extends Item> itemClass)
	{
		Map<String, AttributeMode> ret = new HashMap<>();
		final Map<String, AttributeMode> attr = DEFAULT_INITIAL_ATTRIBUTES.get(itemClass.getName());
		if (attr != null)
		{
			ret.putAll(attr);
		}
		return ret;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryFetchURL</code> attribute.
	 * @return the cloudinaryFetchURL
	 */
	public String getCloudinaryFetchURL(final SessionContext ctx, final Media item)
	{
		return (String)item.getProperty( ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYFETCHURL);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryFetchURL</code> attribute.
	 * @return the cloudinaryFetchURL
	 */
	public String getCloudinaryFetchURL(final Media item)
	{
		return getCloudinaryFetchURL( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryFetchURL</code> attribute. 
	 * @param value the cloudinaryFetchURL
	 */
	public void setCloudinaryFetchURL(final SessionContext ctx, final Media item, final String value)
	{
		item.setProperty(ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYFETCHURL,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryFetchURL</code> attribute. 
	 * @param value the cloudinaryFetchURL
	 */
	public void setCloudinaryFetchURL(final Media item, final String value)
	{
		setCloudinaryFetchURL( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryPublicId</code> attribute.
	 * @return the cloudinaryPublicId
	 */
	public String getCloudinaryPublicId(final SessionContext ctx, final Media item)
	{
		return (String)item.getProperty( ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYPUBLICID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryPublicId</code> attribute.
	 * @return the cloudinaryPublicId
	 */
	public String getCloudinaryPublicId(final Media item)
	{
		return getCloudinaryPublicId( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryPublicId</code> attribute. 
	 * @param value the cloudinaryPublicId
	 */
	public void setCloudinaryPublicId(final SessionContext ctx, final Media item, final String value)
	{
		item.setProperty(ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYPUBLICID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryPublicId</code> attribute. 
	 * @param value the cloudinaryPublicId
	 */
	public void setCloudinaryPublicId(final Media item, final String value)
	{
		setCloudinaryPublicId( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryResourceType</code> attribute.
	 * @return the cloudinaryResourceType
	 */
	public EnumerationValue getCloudinaryResourceType(final SessionContext ctx, final Media item)
	{
		return (EnumerationValue)item.getProperty( ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYRESOURCETYPE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryResourceType</code> attribute.
	 * @return the cloudinaryResourceType
	 */
	public EnumerationValue getCloudinaryResourceType(final Media item)
	{
		return getCloudinaryResourceType( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryResourceType</code> attribute. 
	 * @param value the cloudinaryResourceType
	 */
	public void setCloudinaryResourceType(final SessionContext ctx, final Media item, final EnumerationValue value)
	{
		item.setProperty(ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYRESOURCETYPE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryResourceType</code> attribute. 
	 * @param value the cloudinaryResourceType
	 */
	public void setCloudinaryResourceType(final Media item, final EnumerationValue value)
	{
		setCloudinaryResourceType( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryTransformation</code> attribute.
	 * @return the cloudinaryTransformation
	 */
	public String getCloudinaryTransformation(final SessionContext ctx, final Media item)
	{
		return (String)item.getProperty( ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYTRANSFORMATION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryTransformation</code> attribute.
	 * @return the cloudinaryTransformation
	 */
	public String getCloudinaryTransformation(final Media item)
	{
		return getCloudinaryTransformation( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryTransformation</code> attribute. 
	 * @param value the cloudinaryTransformation
	 */
	public void setCloudinaryTransformation(final SessionContext ctx, final Media item, final String value)
	{
		item.setProperty(ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYTRANSFORMATION,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryTransformation</code> attribute. 
	 * @param value the cloudinaryTransformation
	 */
	public void setCloudinaryTransformation(final Media item, final String value)
	{
		setCloudinaryTransformation( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryType</code> attribute.
	 * @return the cloudinaryType
	 */
	public EnumerationValue getCloudinaryType(final SessionContext ctx, final Media item)
	{
		return (EnumerationValue)item.getProperty( ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYTYPE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryType</code> attribute.
	 * @return the cloudinaryType
	 */
	public EnumerationValue getCloudinaryType(final Media item)
	{
		return getCloudinaryType( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryType</code> attribute. 
	 * @param value the cloudinaryType
	 */
	public void setCloudinaryType(final SessionContext ctx, final Media item, final EnumerationValue value)
	{
		item.setProperty(ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYTYPE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryType</code> attribute. 
	 * @param value the cloudinaryType
	 */
	public void setCloudinaryType(final Media item, final EnumerationValue value)
	{
		setCloudinaryType( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryURL</code> attribute.
	 * @return the cloudinaryURL
	 */
	public String getCloudinaryURL(final SessionContext ctx, final Media item)
	{
		return (String)item.getProperty( ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYURL);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.cloudinaryURL</code> attribute.
	 * @return the cloudinaryURL
	 */
	public String getCloudinaryURL(final Media item)
	{
		return getCloudinaryURL( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryURL</code> attribute. 
	 * @param value the cloudinaryURL
	 */
	public void setCloudinaryURL(final SessionContext ctx, final Media item, final String value)
	{
		item.setProperty(ctx, CloudinarymediacoreConstants.Attributes.Media.CLOUDINARYURL,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.cloudinaryURL</code> attribute. 
	 * @param value the cloudinaryURL
	 */
	public void setCloudinaryURL(final Media item, final String value)
	{
		setCloudinaryURL( getSession().getSessionContext(), item, value );
	}
	
	public CloudinaryConfig createCloudinaryConfig(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType("CloudinaryConfig");
			return (CloudinaryConfig)type.newInstance( ctx, attributeValues );
		}
		catch( JaloGenericCreationException e)
		{
			final Throwable cause = e.getCause();
			throw (cause instanceof RuntimeException ?
			(RuntimeException)cause
			:
			new JaloSystemException( cause, cause.getMessage(), e.getErrorCode() ) );
		}
		catch( JaloBusinessException e )
		{
			throw new JaloSystemException( e ,"error creating CloudinaryConfig : "+e.getMessage(), 0 );
		}
	}
	
	public CloudinaryConfig createCloudinaryConfig(final Map attributeValues)
	{
		return createCloudinaryConfig( getSession().getSessionContext(), attributeValues );
	}
	
	public static final CloudinarymediacoreManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (CloudinarymediacoreManager) em.getExtension(CloudinarymediacoreConstants.EXTENSIONNAME);
	}
	
	@Override
	public String getName()
	{
		return CloudinarymediacoreConstants.EXTENSIONNAME;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.isCloudinaryFetch</code> attribute.
	 * @return the isCloudinaryFetch
	 */
	public Boolean isIsCloudinaryFetch(final SessionContext ctx, final Media item)
	{
		return (Boolean)item.getProperty( ctx, CloudinarymediacoreConstants.Attributes.Media.ISCLOUDINARYFETCH);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.isCloudinaryFetch</code> attribute.
	 * @return the isCloudinaryFetch
	 */
	public Boolean isIsCloudinaryFetch(final Media item)
	{
		return isIsCloudinaryFetch( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.isCloudinaryFetch</code> attribute. 
	 * @return the isCloudinaryFetch
	 */
	public boolean isIsCloudinaryFetchAsPrimitive(final SessionContext ctx, final Media item)
	{
		Boolean value = isIsCloudinaryFetch( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.isCloudinaryFetch</code> attribute. 
	 * @return the isCloudinaryFetch
	 */
	public boolean isIsCloudinaryFetchAsPrimitive(final Media item)
	{
		return isIsCloudinaryFetchAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.isCloudinaryFetch</code> attribute. 
	 * @param value the isCloudinaryFetch
	 */
	public void setIsCloudinaryFetch(final SessionContext ctx, final Media item, final Boolean value)
	{
		item.setProperty(ctx, CloudinarymediacoreConstants.Attributes.Media.ISCLOUDINARYFETCH,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.isCloudinaryFetch</code> attribute. 
	 * @param value the isCloudinaryFetch
	 */
	public void setIsCloudinaryFetch(final Media item, final Boolean value)
	{
		setIsCloudinaryFetch( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.isCloudinaryFetch</code> attribute. 
	 * @param value the isCloudinaryFetch
	 */
	public void setIsCloudinaryFetch(final SessionContext ctx, final Media item, final boolean value)
	{
		setIsCloudinaryFetch( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.isCloudinaryFetch</code> attribute. 
	 * @param value the isCloudinaryFetch
	 */
	public void setIsCloudinaryFetch(final Media item, final boolean value)
	{
		setIsCloudinaryFetch( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.isCloudinaryOverride</code> attribute.
	 * @return the isCloudinaryOverride
	 */
	public Boolean isIsCloudinaryOverride(final SessionContext ctx, final Media item)
	{
		return (Boolean)item.getProperty( ctx, CloudinarymediacoreConstants.Attributes.Media.ISCLOUDINARYOVERRIDE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.isCloudinaryOverride</code> attribute.
	 * @return the isCloudinaryOverride
	 */
	public Boolean isIsCloudinaryOverride(final Media item)
	{
		return isIsCloudinaryOverride( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.isCloudinaryOverride</code> attribute. 
	 * @return the isCloudinaryOverride
	 */
	public boolean isIsCloudinaryOverrideAsPrimitive(final SessionContext ctx, final Media item)
	{
		Boolean value = isIsCloudinaryOverride( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Media.isCloudinaryOverride</code> attribute. 
	 * @return the isCloudinaryOverride
	 */
	public boolean isIsCloudinaryOverrideAsPrimitive(final Media item)
	{
		return isIsCloudinaryOverrideAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.isCloudinaryOverride</code> attribute. 
	 * @param value the isCloudinaryOverride
	 */
	public void setIsCloudinaryOverride(final SessionContext ctx, final Media item, final Boolean value)
	{
		item.setProperty(ctx, CloudinarymediacoreConstants.Attributes.Media.ISCLOUDINARYOVERRIDE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.isCloudinaryOverride</code> attribute. 
	 * @param value the isCloudinaryOverride
	 */
	public void setIsCloudinaryOverride(final Media item, final Boolean value)
	{
		setIsCloudinaryOverride( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.isCloudinaryOverride</code> attribute. 
	 * @param value the isCloudinaryOverride
	 */
	public void setIsCloudinaryOverride(final SessionContext ctx, final Media item, final boolean value)
	{
		setIsCloudinaryOverride( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Media.isCloudinaryOverride</code> attribute. 
	 * @param value the isCloudinaryOverride
	 */
	public void setIsCloudinaryOverride(final Media item, final boolean value)
	{
		setIsCloudinaryOverride( getSession().getSessionContext(), item, value );
	}
	
}
