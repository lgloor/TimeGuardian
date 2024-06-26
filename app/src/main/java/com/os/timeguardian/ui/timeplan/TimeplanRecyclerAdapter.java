package com.os.timeguardian.ui.timeplan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.os.timeguardian.R;
import com.os.timeguardian.model.AppTimeplanModel;
import com.os.timeguardian.utils.PackageUtil;

import java.util.List;

import kotlin.Suppress;

// This class with his methods was created with the help of ChatGpt.

public class TimeplanRecyclerAdapter extends RecyclerView.Adapter<TimeplanRecyclerAdapter.ViewHolder> {

    Context context;
    List<AppTimeplanModel> models;

    public TimeplanRecyclerAdapter(Context context, List<AppTimeplanModel> models) {
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
        View v = inflater.inflate(R.layout.time_recycler_view_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String packageName = models.get(position).getPackageName();
        holder.imageView.setImageDrawable(getIcon(packageName));
        String appName = PackageUtil.getUserFriendlyAppName(packageName, context);
        String punishmentLevel = models.get(position).getPunishmentLevel();
        holder.appNameView.setText(appName);
        holder.appTimeView.setText(punishmentLevel);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getIcon(String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch(PackageManager.NameNotFoundException e) {
            return context.getDrawable(R.drawable.ic_launcher_foreground);
        }
    }
    @Override
    public int getItemCount() {
        return this.models.size();
    }
}

