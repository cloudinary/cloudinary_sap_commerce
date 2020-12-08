package uk.ptr.cloudinary.dto;

import java.util.List;

public class CloudinaryProductAssestData {

    private String productCode;
    private List<MediaContainerData> mediaContainers;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public List<MediaContainerData> getMediaContainers() {
        return mediaContainers;
    }

    public void setMediaContainers(List<MediaContainerData> mediaContainers) {
        this.mediaContainers = mediaContainers;
    }
}
