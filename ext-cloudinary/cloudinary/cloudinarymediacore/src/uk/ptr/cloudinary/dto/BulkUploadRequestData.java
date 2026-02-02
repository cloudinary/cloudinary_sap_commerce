package uk.ptr.cloudinary.dto;

import java.util.List;

public class BulkUploadRequestData {

    private List<CloudinaryProductAssestData> productMediaAssest;


    public List<CloudinaryProductAssestData> getProductMediaAssest() {
        return productMediaAssest;
    }

    public void setProductMediaAssest(List<CloudinaryProductAssestData> productMediaAssest) {
        this.productMediaAssest = productMediaAssest;
    }

}
