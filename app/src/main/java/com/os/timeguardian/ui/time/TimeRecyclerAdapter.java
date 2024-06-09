package com.os.timeguardian.ui.time;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.os.timeguardian.R;
import com.os.timeguardian.model.AppTimeModel;
import com.os.timeguardian.utils.PackageUtil;

import java.util.List;

public class TimeRecyclerAdapter extends RecyclerView.Adapter<TimeRecyclerAdapter.ViewHolder> {
    Context context;
    List<AppTimeModel> models;

    public TimeRecyclerAdapter(Context context, List<AppTimeModel> models) {
        this.context = context;
        this.models = models;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView appNameView, appTimeView;
        ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.appIconView);
            this.appNameView = itemView.findViewById(R.id.appName);
            this.appTimeView = itemView.findViewById(R.id.appTime);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.time_recycler_view_row ,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String packageName = models.get(position).getPackageName();
        holder.imageView.setImageDrawable(getIcon(packageName));
        String appName = PackageUtil.getUserFriendlyAppName(packageName, context);
        long appTime = models.get(position).getAppTime();
        holder.appNameView.setText(appName);
        holder.appTimeView.setText(formatTime(appTime));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getIcon(String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return context.getDrawable(R.drawable.ic_launcher_foreground);
        }
    }

    @SuppressLint("DefaultLocale")
    public static String formatTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02dh %02dmin %02dsec", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02dmin %02dsec", minutes, seconds);
        } else {
            return String.format("%02dsec", seconds);
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
