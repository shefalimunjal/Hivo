package edu.sjsu.hivo.model;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/* Property model, deserialized by Gson. */
public class Property {

    private static final String AWS_IMAGE_ENDPOINT = "https://project-realestate.herokuapp.com/property_image?image_url=";

    @SerializedName("ZIP")
    private String zip;

    @SerializedName("PROPERTY TYPE")
    private String propertyType;

    @SerializedName("STATE")
    private String state;

    @SerializedName("SALE TYPE")
    private String saleType;

    @SerializedName("CITY")
    private String city;

    @SerializedName("INTERESTED")
    private String interested;

    @SerializedName("PRICE")
    private String price;

    @SerializedName("ADDRESS")
    private String address;

    @SerializedName("LOT SIZE")
    private String lotSize = "3,000";

    @SerializedName("SQUARE FEET")
    private String area = "1,086";

    @SerializedName("BUILT")
    private String built = "1962";

    @SerializedName("BEDS")
    private String beds = "3";

    @SerializedName("BATHS")
    private String baths = "2";

    private String id;

    @SerializedName("FAVORITE")
    private String favorite;

    @SerializedName("BOUNDARY")
    private Boundary boundary;

    @SerializedName("image_url")
    private List<String> urls;

    @SerializedName("COST_SQUARE_FEET")
    private String pricePerSqFt;


    private String predictedPrice;
    private String displayUrl;

    private static List<String> townHouseUrls;
    private static List<String> condoUrls;
    private static List<String> singleFamilyUrls;


    public Property() {

    }

    public String getPredictedPrice(){
        String price = "Loading...";
        if (!TextUtils.isEmpty(predictedPrice)) {
            price = predictedPrice;
            if (!price.equals("Not Known")) {

                NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
                format.setCurrency(Currency.getInstance("USD"));
                format.setMinimumFractionDigits(0);
                price = format.format(Double.parseDouble(price));
            }
        }
        return "Estimated price: " + price;
    }

    public void setPredictedPrice(String predictedPrice){
        this.predictedPrice = predictedPrice;
    }
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInterested() {
        return interested;
    }

    public void setInterested(String interested) {
        this.interested = interested;
    }

    public String getPrice() {
        if (!TextUtils.isEmpty(price)) {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
            format.setCurrency(Currency.getInstance("USD"));
            format.setMinimumFractionDigits(0);
            return format.format(Double.parseDouble(price));
        }
        return "Not known";
    }

    public String getFormattedPriceForMap(){
        if(!price.equals("Not known")){
            String dollarPrice = "$" + price;
            String price1 = dollarPrice.substring(1);
            int intPrice = Integer.parseInt(price1);
            String makePrice = "";

            if(intPrice >= 1000 && intPrice < 1000000){
                intPrice = intPrice/1000;
                makePrice = String.valueOf(intPrice) +'k';
            }

            if(intPrice >= 1000000 && intPrice < 1000000000){
                intPrice = intPrice/1000000;
                makePrice = String.valueOf(intPrice) +'M';
            }

            return makePrice;


        }
        return "";
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return city + ", " + state + " " + zip;
    }

    public String getLotSize() {
        if (!TextUtils.isEmpty(lotSize)) {
            return lotSize;
        }
        return "Not Known";
    }

    public void setLotSize(String lotSize) {
        this.lotSize = lotSize;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBuilt() {
        return built;
    }

    public void setBuilt(String built) {
        this.built = built;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }

    public String getBaths() {
        return baths;
    }

    public void setBaths(String baths) {
        this.baths = baths;
    }

    public String getPricePerSqFt(){
        return pricePerSqFt;
    }

    public void setPricePerSqFt(String pricePerSqFt){
        this.pricePerSqFt = pricePerSqFt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public Boundary getBoundary() {
        return boundary;
    }

    public void setBoundary(Boundary boundary) {
        this.boundary = boundary;
    }

    public boolean isLocationAvailable() {
        return boundary != null &&
                boundary.getCoordinates() != null &&
                boundary.getCoordinates().size() == 2;
    }

    public double getLatitude() {
        if (isLocationAvailable()) {
            return boundary.getCoordinates().get(1);
        }
        return 0.0;
    }

    public double getLongitude() {
        if (isLocationAvailable()) {
            return boundary.getCoordinates().get(0);
        }
        return 0.0;
    }

    // Dummy images for now
    public List<String> getUrls() {
        if (urls == null || urls.size() <= 0) {
            urls = getShuffeledClientUrls();
        }
        urls = sanitizeUrls();
        return urls;
    }

    private List<String> getShuffeledClientUrls() {
        List<String> urls = null;

        if(getPropertyType().equals("Townhouse")){
            urls = getTownHouseUrls();
        } else if(getPropertyType().equals("Condo/Co-op")){
            urls = getCondoUrls();
        } else{
            urls = getSingleFamilyUrls();
        }

        Collections.shuffle(urls);
        return urls;
    }

    private List<String> sanitizeUrls() {
        List<String> sanitizedUrls = new ArrayList<>();
        for (String url : urls) {
            if (url.contains("http")) {
                sanitizedUrls.add(url);
            } else {
                sanitizedUrls.add(AWS_IMAGE_ENDPOINT + url);
            }
        }

        return sanitizedUrls;
    }

    public static List<String> getTownHouseUrls() {

        if (townHouseUrls == null) {
            townHouseUrls = new ArrayList<>();
            Collections.addAll(townHouseUrls, "https://ssl.cdn-redfin.com/photo/8/bigphoto/235/ML81715235_1.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/649/ML81728649_5.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/447/ML81730447_0.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/555/ML81730555_3.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/087/ML81731087_1.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/739/ML81726739_6.jpg",
                    "https://ssl.cdn-redfin.com/photo/216/bigphoto/503/Plan-437503_0.jpg",
                    "https://ssl.cdn-redfin.com/photo/216/bigphoto/078/Plan-463078_0.jpg");

        }
        return townHouseUrls;
    }

    public static List<String> getCondoUrls() {

        if (condoUrls == null) {
            condoUrls = new ArrayList<>();
            Collections.addAll(condoUrls, "https://ssl.cdn-redfin.com/photo/8/bigphoto/078/ML81726078_C.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/515/ML81721515_3_4.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/902/ML81729902_6.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/357/ML81731357_1.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/406/ML81725406_6.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/763/ML81729763_2.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/166/ML81723166_6.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/345/ML81726345_8.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/504/ML81724504_2.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/548/ML81730548_4.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/254/ML81728254_2.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/893/ML81727893_5.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/747/ML81714747_B.jpg",
            "https://ssl.cdn-redfin.com/system_files/media/228107_JPG/item_1.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/959/ML81731959_0.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/867/ML81729867_4.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/010/ML81732010_1.jpg",
            "https://ssl.cdn-redfin.com/photo/8/bigphoto/308/ML81731308_3.jpg");
        }
        return condoUrls;
    }

    public static List<String> getSingleFamilyUrls() {

        if (singleFamilyUrls == null) {
            singleFamilyUrls = new ArrayList<>();
            Collections.addAll(singleFamilyUrls, "https://ssl.cdn-redfin.com/photo/8/bigphoto/901/ML81729901_3.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/718/ML81731718_0.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/392/ML81731392_0.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/mbpaddedwide/677/genMid.ML81726677_H.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/mbpaddedwide/076/genMid.ML81732076_0.jpg",
                    "https://ssl.cdn-redfin.com/photo/10/mbpaddedwide/701/genMid.40842701_4.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/348/ML81727348_8.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/205/ML81731205_8.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/131/ML81731131_2.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/845/ML81719845_9.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/121/ML81731121_2.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/772/ML81729772_1.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/772/ML81729772_1.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/479/ML81723479_0.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/821/ML81731821_0.jpg",
                    "https://ssl.cdn-redfin.com/photo/8/bigphoto/568/ML81731568_0.jpg");
        }
        return singleFamilyUrls;
    }

    public void setUrls(List<String> urls){
        this.urls = urls;
        Collections.shuffle(urls);
    }

    public String getDisplayUrl() {
        List<String> urls = getShuffeledClientUrls();
        if (urls != null && urls.size() > 0 && TextUtils.isEmpty(displayUrl)) {
            Random random = new Random();
            int randomIndex = random.nextInt(urls.size());
            displayUrl =  urls.get(randomIndex);
        }

        return displayUrl;
    }

    private static class Boundary {
        private List<Double> coordinates;

        public List<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Double> coordinates) {
            this.coordinates = coordinates;
        }
    }
}


