package edu.cmu.demoapp2;

/**
 * Created by Yu-Lun Tsai on 31/07/2017.
 */

public class RestaurantInfoCell {

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    private String mName;
    private String mDescription;
    private String mImageUrl;

    public RestaurantInfoCell(String name, String description, String url){
        mName = name;
        mDescription = description;
        mImageUrl = url;
    }
}
