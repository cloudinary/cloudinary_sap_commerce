package uk.ptr.cloudinary.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UplaodApiResponseData {

    private String signature;
    private String format;
    private String resource_type;
    private String type;
    private String overwritten;
    private String secure_url;
    private String original_filename;
    private String public_id;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOverwritten() {
        return overwritten;
    }

    public void setOverwritten(String overwritten) {
        this.overwritten = overwritten;
    }

    public String getSecure_url() {
        return secure_url;
    }

    public void setSecure_url(String secure_url) {
        this.secure_url = secure_url;
    }

    public String getOriginal_filename() {
        return original_filename;
    }

    public void setOriginal_filename(String original_filename) {
        this.original_filename = original_filename;
    }

    public String getPublic_id() {
        return public_id;
    }

    public void setPublic_id(String public_id) {
        this.public_id = public_id;
    }
}
