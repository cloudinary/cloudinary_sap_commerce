package uk.ptr.cloudinary.util;

import java.util.Arrays;
import java.util.List;

public class CloudinaryConfigUtils {

    public static String updateMediaCloudinaryUrl(String cloudinaryUrl, String domainName) {
        if (domainName != null) {
            String data[] = cloudinaryUrl.split("//")[1].split("/");
            data[0] = domainName;
            List<String> dataList = Arrays.asList(data);
            return String.join("/", dataList);
        }
        return cloudinaryUrl;
    }
}
