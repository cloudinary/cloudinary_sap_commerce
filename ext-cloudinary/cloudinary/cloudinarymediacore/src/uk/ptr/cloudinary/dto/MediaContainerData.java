package uk.ptr.cloudinary.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MediaContainerData {

    private String mediaContainerCode;
    @NotNull
    private String publicId;
    @NotNull
    private String resourceType;
    @NotNull
    private String cloudinaryType;
    @NotNull
    private String cloudinaryMediaFormat;

    public String getMediaContainerCode() {
        return mediaContainerCode;
    }

    public void setMediaContainerCode(String mediaContainerCode) {
        this.mediaContainerCode = mediaContainerCode;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getCloudinaryType() {
        return cloudinaryType;
    }

    public void setCloudinaryType(String cloudinaryType) {
        this.cloudinaryType = cloudinaryType;
    }

    public String getCloudinaryMediaFormat() {
        return cloudinaryMediaFormat;
    }

    public void setCloudinaryMediaFormat(String cloudinaryMediaFormat) {
        this.cloudinaryMediaFormat = cloudinaryMediaFormat;
    }
}
