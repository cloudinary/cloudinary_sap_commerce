package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.cmswebservices.data.MediaData;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller that provides media.
 */
@Controller
@RequestMapping("{baseSiteId}/catalogs/{catalogId}/versions/{versionId}" + CloudinaryMediaUploadController.MEDIA_URI_PATH)
@Api(tags = "catalog version media")
public class CloudinaryMediaUploadController {
    public static final String MEDIA_URI_PATH = "/media";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiOperation(value = "Uploads media.", notes = "Provides a new multipart media item for a given catalogId.", nickname = "doUploadMultipartMedia")
    @ApiResponses(
            { //
                    @ApiResponse(code = 400, message = "When an error occurs parsing the MultipartFile (IOException) or when the media query parameters provided contain validation errors (WebserviceValidationException)"),
                    @ApiResponse(code = 200, message = "The newly created Media item", response = MediaData.class) })
    @ApiImplicitParams(
            { //
                    @ApiImplicitParam(name = "altText", value = "The alternative text to use for the newly created media.", required = true, dataType = "string", paramType = "query"),
                    @ApiImplicitParam(name = "code", value = "The code to use for the newly created media.", required = true, dataType = "string", paramType = "query"),
                    @ApiImplicitParam(name = "description", value = "The description to use for the newly created media.", required = true, dataType = "string", paramType = "query"),
                    @ApiImplicitParam(name = "mime", value = "Internet Media Type for the media file.", required = false, dataType = "string", paramType = "query")})
    public MediaData uploadMultipartMedia(
            @ApiParam(value = "The unique identifier of the catalog for which to link the new media.", required = true) //
            @PathVariable("catalogId")
            final String catalogId,
            @ApiParam(value = "The specific catalog version to which the new media will be associated to.", required = true) //
            @PathVariable("versionId")
            final String versionId,
            @ApiIgnore(value = "The MediaData containing the data for the associated media item to be created.") //
            @ModelAttribute("media")
            final MediaData media,
            @ApiParam(value = "The file representing the actual binary contents of the media to be created.", required = true) //
            @RequestParam("file")
            final MultipartFile multiPart, //
            final HttpServletRequest request, final HttpServletResponse response) throws IOException
    {
        return null;
    }
}
