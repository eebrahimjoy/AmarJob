package amarjob.com.retrofit;

import amarjob.com.model.PlaceAPI.Predictions;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("place/autocomplete/json")
    public Call<Predictions> getPlacesAutoComplete(
            @Query("input") String input,
            @Query("types") String types,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("strictbounds") String strictbounds,
            @Query("key") String key
    );
}
