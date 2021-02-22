package uk.ptr.cloudinary.v2.controller;


import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.zookeeper.proto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.ptr.cloudinary.dto.BulkUploadRequestData;
import uk.ptr.cloudinary.service.BulkUploadApiService;
import uk.ptr.cloudinary.validator.BulkUploadValidator;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "{baseSiteId}/bulkupload")
@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
@Api(tags = "Bulk Upload")
public class BulkUploadAssetController {

    @Resource
    private BulkUploadApiService bulkUploadApiService;

    @Resource(name = "bulkUploadValidator")
    private BulkUploadValidator bulkUploadValidator;

    @RequestMapping(value = "/assets", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiBaseSiteIdParam
    public ResponseEntity<String> uploadAssets(@ApiParam(value = "Base site identifier", required = true) @PathVariable final String baseSiteId, @RequestBody @Valid final BulkUploadRequestData resquestBody, final BindingResult bindingResult) {

        bulkUploadValidator.validate(resquestBody, bindingResult);
        if(bindingResult.hasErrors())
        {
            return new ResponseEntity<>(bindingResult.getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        else {
            bulkUploadApiService.bulkAssetUpload(resquestBody, baseSiteId);
        }
        return new ResponseEntity<>("Uploaded Assets", HttpStatus.OK);
    }

}


