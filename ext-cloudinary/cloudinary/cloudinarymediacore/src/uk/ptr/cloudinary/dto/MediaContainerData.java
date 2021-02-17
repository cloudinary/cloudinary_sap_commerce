package uk.ptr.cloudinary.dto;

public class MediaContainerData {

    private String mediaContainerCode;
    private String url;
    private String publicId;
    private String resourceType;
    private String cloudinaryType;

    public String getCloudinaryMediaFormat() {
        return cloudinaryMediaFormat;
    }

    public void setCloudinaryMediaFormat(String cloudinaryMediaFormat) {
        this.cloudinaryMediaFormat = cloudinaryMediaFormat;
    }

    private String cloudinaryMediaFormat;

    public String getMediaContainerCode() {
        return mediaContainerCode;
    }

    public void setMediaContainerCode(String mediaContainerCode) {
        this.mediaContainerCode = mediaContainerCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
