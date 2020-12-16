package uk.ptr.cloudinary.service;

import uk.ptr.cloudinary.dto.BulkUploadRequestData;


public interface BulkUploadApiService {

    public void bulkAssetUpload(BulkUploadRequestData bulkUploadRequestData, String baseSiteId);
}
