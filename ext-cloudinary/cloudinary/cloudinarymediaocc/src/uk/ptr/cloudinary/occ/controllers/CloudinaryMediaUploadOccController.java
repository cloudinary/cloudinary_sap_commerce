package uk.ptr.cloudinary.occ.controllers;

import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.media.MediaFacade;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ptr.cloudinary.facades.CloudinaryMediaFacade;

import jakarta.annotation.Resource;
import java.io.IOException;

/**
 * Controller that provides media.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/catalogs/{catalogId}/versions/{versionId}" + CloudinaryMediaUploadOccController.MEDIA_URI_PATH)
@Tag(name = "catalog version media")
public class CloudinaryMediaUploadOccController
{

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudinaryMediaUploadOccController.class);
    public static final String MEDIA_URI_PATH = "/cloudinaryMedia";

    @Resource
    CloudinaryMediaFacade cloudinaryMediaFacade;

    @Resource
    private MediaFacade mediaFacade;

    @Resource
    private LocationHeaderResource locationHeaderResource;

    @Resource
    private DataMapper dataMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Operation(summary = "Uploads media.", description = "Provides a new multipart media item for a given catalogId.", operationId = "doUploadMultipartMedia")
    @ApiResponse(responseCode = "400", description = "When an error occurs parsing the MultipartFile (IOException) or when the media query parameters provided contain validation errors (WebserviceValidationException)")
    @ApiResponse(responseCode = "200", description = "The newly created Media item")
    @ApiBaseSiteIdParam
    public MediaData uploadMultipartMedia(
            @Parameter(description = "The unique identifier of the catalog for which to link the new media.", required = true) //
            @PathVariable("catalogId")
            final String catalogId,
            @Parameter(description = "The specific catalog version to which the new media will be associated to.", required = true) //
            @PathVariable("versionId")
            final String versionId,
            @Parameter(description = "The file representing the actual binary contents of the media to be created.", required = true) //
            @RequestParam("file") final MultipartFile multiPart,
            @Parameter(description = "The MediaData containing the data for the associated media item to be created.", hidden = true) //
            @ModelAttribute("media") final MediaData media) throws IOException
    {
        media.setCatalogId(catalogId);
        media.setCatalogVersion(versionId);

        try
        {
            final MediaData convertedMediaData = dataMapper.map(media, MediaData.class);
            final MediaData newMedia = cloudinaryMediaFacade.addMedia(convertedMediaData);

            return dataMapper.map(newMedia, MediaData.class);
        }
        catch (final ValidationException e)
        {
            LOGGER.info("Validation exception", e);
            throw new WebserviceValidationException(e.getValidationObject());
        }

    }


}
