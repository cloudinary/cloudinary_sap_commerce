package uk.ptr.cloudinary.service;

public interface RemoveTagApiService {

    public void removeTagFromAsset(String publicId, String productCode, String cloudinaryURL);
}
