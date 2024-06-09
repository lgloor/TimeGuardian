package com.os.timeguardian.ui.openings;

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
import com.os.timeguardian.model.AppOpeningsModel;
import com.os.timeguardian.utils.PackageUtil;

import java.util.List;

public class OpeningsRecyclerAdapter extends RecyclerView.Adapter<OpeningsRecyclerAdapter.ViewHolder> {

    Context context;
    List<AppOpeningsModel> models;

    public OpeningsRecyclerAdapter(Context context, List<AppOpeningsModel> models) {
        this.context = context;
        this.models = models;
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
        String userFriendlyAppName = PackageUtil.getUserFriendlyAppName(packageName, context);
        int openings = models.get(position).getAppOpenings();
        holder.appNameView.setText(userFriendlyAppName);
        holder.openingsView.setText(formatOpenings(openings));
    }

    @SuppressLint("DefaultLocale")
    private String formatOpenings(int openings) {
        return String.format("Opened %dx", openings);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getIcon(String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return context.getDrawable(R.drawable.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView appNameView, openingsView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.appIconView);
            this.appNameView = itemView.findViewById(R.id.appName);
            this.openingsView = itemView.findViewById(R.id.appTime);
        }
    }
}
