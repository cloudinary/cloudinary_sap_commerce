package uk.ptr.cloudinary.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudinaryDerivedAssetData {

    private String url;
    private String secure_url;
    private String raw_transformation;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecure_url() {
        return secure_url;
    }

    public void setSecure_url(String secure_url) {
        this.secure_url = secure_url;
    }

    public String getRaw_transformation() {
        return raw_transformation;
    }

    public void setRaw_transformation(String raw_transformation) {
        this.raw_transformation = raw_transformation;
    }
}
