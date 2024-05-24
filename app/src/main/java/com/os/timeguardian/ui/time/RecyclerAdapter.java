package com.os.timeguardian.ui.time;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.os.timeguardian.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    ArrayList<String> apps = new ArrayList<>();
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView appnames;
        TextView secondUnnecessaryTestLol;
        ViewHolder(View itemview) {
            super(itemview);
            this.image = itemview.findViewById(R.id.imageView);
            this.appnames = itemview.findViewById(R.id.textView1);
            this.secondUnnecessaryTestLol = itemview.findViewById(R.id.textView2);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_time ,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
}
