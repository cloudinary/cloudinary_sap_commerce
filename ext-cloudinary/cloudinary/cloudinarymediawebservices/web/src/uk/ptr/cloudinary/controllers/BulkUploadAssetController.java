package uk.ptr.cloudinary.controllers;


import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ptr.cloudinary.dto.BulkUploadRequestData;
import uk.ptr.cloudinary.service.BulkUploadApiService;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "{baseSiteId}/bulkupload")
@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
@Api(tags = "Bulk Upload")
public class BulkUploadAssetController {

    @Resource
    private BulkUploadApiService bulkUploadApiService;

    @RequestMapping(value = "/assets", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiBaseSiteIdParam
    public ResponseEntity<String> uploadAssets(@ApiParam(value = "Base site identifier", required = true) @PathVariable final String baseSiteId,@RequestBody final BulkUploadRequestData resquestBody) {

       bulkUploadApiService.bulkAssetUpload(resquestBody, baseSiteId);
       return new ResponseEntity<>("Uploaded Assets", HttpStatus.OK);
    }

}
