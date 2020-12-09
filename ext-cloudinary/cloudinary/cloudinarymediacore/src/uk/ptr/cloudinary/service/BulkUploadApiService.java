package uk.ptr.cloudinary.service;

import uk.ptr.cloudinary.dto.BulkUploadRequestData;
import uk.ptr.cloudinary.dto.BulkUploadResponseData;


public interface BulkUploadApiService {

    public void bulkAssetUpload(BulkUploadRequestData bulkUploadRequestData);
}
