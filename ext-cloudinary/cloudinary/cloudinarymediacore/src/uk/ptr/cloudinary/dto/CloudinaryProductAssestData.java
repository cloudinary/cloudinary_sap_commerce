package uk.ptr.cloudinary.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CloudinaryProductAssestData {

    @NotNull
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
