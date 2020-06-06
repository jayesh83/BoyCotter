package com.japps.boycotter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.japps.boycotter.R;
import com.japps.boycotter.util.PicassoClient;

import java.util.ArrayList;

public class UninstalledAppAlternativeAdapter extends RecyclerView.Adapter<UninstalledAppAlternativeAdapter.AlternativeAppVH> {
    ArrayList<String> altrAppNames, alterAppPackages, alterAppStars, alterAppDownloads;
    ArrayList<String> alterAppIcons;

    public UninstalledAppAlternativeAdapter(ArrayList<String> altrAppNames,
                                            ArrayList<String> alterAppPackages, ArrayList<String> alterAppStars,
                                            ArrayList<String> alterAppDownloads, ArrayList<String> alterAppIcons) {
        this.altrAppNames = altrAppNames;
        this.alterAppPackages = alterAppPackages;
        this.alterAppStars = alterAppStars;
        this.alterAppDownloads = alterAppDownloads;
        this.alterAppIcons = alterAppIcons;
    }

    @NonNull
    @Override
    public AlternativeAppVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alternative_apps_item, parent, false);
        return new AlternativeAppVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlternativeAppVH holder, int position) {
        if (altrAppNames != null)
            holder.appName.setText(altrAppNames.get(position));

        if (alterAppStars != null)
            holder.star.setText(alterAppStars.get(position));

        if (alterAppDownloads != null)
            holder.downloads.setText(alterAppDownloads.get(position));

        PicassoClient.downloadImage(alterAppIcons.get(position), holder.icon);
    }

    @Override
    public int getItemCount() {
        return altrAppNames != null ? altrAppNames.size() : 0;
    }

    public class AlternativeAppVH extends RecyclerView.ViewHolder {
        private TextView appName, star, downloads;
        private ImageView icon;
        private Button installBtn;

        public AlternativeAppVH(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.alter_app_name);
            star = itemView.findViewById(R.id.alter_app_star);
            downloads = itemView.findViewById(R.id.alter_app_downloads);
            icon = itemView.findViewById(R.id.alter_app_icon);
            installBtn = itemView.findViewById(R.id.app_uninstall_btn);
        }
    }
}
