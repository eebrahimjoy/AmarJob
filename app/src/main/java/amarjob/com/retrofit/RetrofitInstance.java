package amarjob.com.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static String BASE_URL="";
    private static String BASE_URL_PLACE_API="https://maps.googleapis.com/maps/api/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(){

        if (retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

    public static Retrofit getRetrofitInstanceForPlaceAPI(){

        if (retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL_PLACE_API).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
