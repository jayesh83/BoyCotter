package com.japps.boycotter.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.japps.boycotter.R;

import java.util.ArrayList;

public class InstalledAppsListAdapter extends RecyclerView.Adapter<InstalledAppsListAdapter.InstalledAppsListViewHolder> {
    private ArrayList<String> installed_chini_apps;
    private ArrayList<String> installed_chini_apps_package;
    private ArrayList<Drawable> installed_chini_apps_icon;
    private onUninstallClickListener listener;

    public InstalledAppsListAdapter(ArrayList<String> appNames, ArrayList<Drawable> icons, ArrayList<String> packages) {
        this.installed_chini_apps = appNames;
        this.installed_chini_apps_package = packages;
        this.installed_chini_apps_icon = icons;
    }

    @NonNull
    @Override
    public InstalledAppsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        return new InstalledAppsListViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.installed_apps_item;
    }

    @Override
    public void onBindViewHolder(@NonNull InstalledAppsListViewHolder holder, int position) {
        String appTitle = installed_chini_apps.get(position);
        Drawable appIcon = installed_chini_apps_icon.get(position);
        holder.app_title.setText(appTitle);
        holder.app_icon.setImageDrawable(appIcon);
    }

    @Override
    public int getItemCount() {
        return installed_chini_apps == null ? 0 : installed_chini_apps.size();
    }

    public void setOnUninstallClickListener(onUninstallClickListener listener) {
        this.listener = listener;
    }

    public interface onUninstallClickListener {
        void onUninstall(int position, String packageId);
    }

    public class InstalledAppsListViewHolder extends RecyclerView.ViewHolder {
        private ImageView app_icon;
        private TextView app_title;

        public InstalledAppsListViewHolder(@NonNull View itemView) {
            super(itemView);
            app_icon = itemView.findViewById(R.id.boyCott_app_icon);
            app_title = itemView.findViewById(R.id.boyCott_app_name);
            itemView.findViewById(R.id.app_uninstall_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onUninstall(getAdapterPosition(), installed_chini_apps_package.get(getAdapterPosition()));
                }
            });
        }
    }
}
