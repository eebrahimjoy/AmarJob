package amarjob.com.view.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import amarjob.com.R;
import amarjob.com.databinding.ModelFilterJobBinding;
import amarjob.com.model.Job;


public class FilterJobAdapter extends RecyclerView.Adapter<FilterJobAdapter.ViewModel> {
    private List<Job> jobs;
    private Context context;

    public FilterJobAdapter(List<Job> jobs, Context context) {
        this.jobs = jobs;
        this.context = context;

    }


    @NonNull
    @Override
    public ViewModel onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ModelFilterJobBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.model_filter_job, viewGroup, false);
        return new ViewModel(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewModel viewModel, int i) {
        final Job job = jobs.get(i);
        viewModel.binding.jobAvailableTV.setText(job.getAvailableJobAmount());
        viewModel.binding.dateTV.setText(job.getDate());
        viewModel.binding.jobTypeTV.setText(job.getType());
        viewModel.binding.titleTV.setText(job.getTitle());


        viewModel.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Under construction", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    @Override
    public int getItemCount() {
        return jobs.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewModel extends RecyclerView.ViewHolder {
        private ModelFilterJobBinding binding;

        public ViewModel(ModelFilterJobBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
