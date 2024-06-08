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

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<AppTimeModel> apps;

    public RecyclerAdapter(Context context, List<AppTimeModel> apps) {
        this.context = context;
        this.apps = apps;
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
        holder.imageView.setImageDrawable(getIcon(apps.get(position).getAppName()));
        String appName = PackageUtil.getUserFriendlyAppName(apps.get(position).getAppName(), context);
        String appTime = apps.get(position).getAppTime();
        holder.appNameView.setText(appName);
        holder.appTimeView.setText(appTime);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getIcon(String appName) {
        try {
            return context.getPackageManager().getApplicationIcon(appName);
        } catch (PackageManager.NameNotFoundException e) {
            return context.getDrawable(R.drawable.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
}
