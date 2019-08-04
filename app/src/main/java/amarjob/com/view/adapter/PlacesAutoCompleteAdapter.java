package amarjob.com.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import amarjob.com.R;
import amarjob.com.model.PlaceAPI.Prediction;
import amarjob.com.model.PlaceAPI.Predictions;
import amarjob.com.retrofit.ApiService;
import amarjob.com.retrofit.RetrofitInstance;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<Prediction> {

    private Context context;
    private List<Prediction> predictions;

    public PlacesAutoCompleteAdapter(Context context, List<Prediction> predictions) {
        super(context, R.layout.model_location_item, predictions);
        this.context = context;
        this.predictions = predictions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_location_item, null);
        if (predictions != null && predictions.size() > 0) {
            Prediction prediction = predictions.get(position);
            TextView textViewName = view.findViewById(R.id.locationNameTV);
            textViewName.setText(prediction.getDescription());
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new PlacesAutoCompleteFilter(this, context);
    }

    private class PlacesAutoCompleteFilter extends Filter {

        private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;
        private Context context;

        public PlacesAutoCompleteFilter(PlacesAutoCompleteAdapter placesAutoCompleteAdapter, Context context) {
            super();
            this.placesAutoCompleteAdapter = placesAutoCompleteAdapter;
            this.context = context;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            try {
                placesAutoCompleteAdapter.predictions.clear();
                FilterResults filterResults = new FilterResults();
                if (charSequence == null || charSequence.length() == 0) {
                    filterResults.values = new ArrayList<Prediction>();
                    filterResults.count = 0;
                } else {
                    ApiService googleMapAPI = RetrofitInstance.getRetrofitInstanceForPlaceAPI().create(ApiService.class);
                    Predictions predictions = googleMapAPI.getPlacesAutoComplete(charSequence.toString(), "establishment", "23.7808875,90.2792371", "500000", "strictbounds", context.getString(R.string.place_api_key)).execute().body();
                    filterResults.values = predictions.getPredictions();
                    filterResults.count = predictions.getPredictions().size();
                }
                return filterResults;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            placesAutoCompleteAdapter.predictions.clear();
            if (filterResults != null && filterResults.count > 0) {
                placesAutoCompleteAdapter.predictions.addAll((List<Prediction>) filterResults.values);
                placesAutoCompleteAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Prediction prediction = (Prediction) resultValue;
            return prediction.getDescription();
        }
    }

}