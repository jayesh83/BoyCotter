package com.japps.boycotter.ui;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.content.MimeTypeFilter;
import androidx.fragment.app.Fragment;

import com.japps.boycotter.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import static com.japps.boycotter.MyApplication.BOYCOTT_PREFERENCE_KEY;
import static com.japps.boycotter.MyApplication.BOYCOTT_SCORE_KEY;
import static com.japps.boycotter.MyApplication.TOTAL_INSTALLED_CHINESE_APPS;

public class AppreciationFragment extends Fragment implements View.OnClickListener {
    private File imagePath;

    public AppreciationFragment() {
    }

    ;
    private Button shareBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int netScore;
        TextView score = view.findViewById(R.id.victory_score);
        TextView uninstallCount = view.findViewById(R.id.victory_uninstall_count);
        TextView appreciation_text = view.findViewById(R.id.appreciation_text);
        TextView boycotter_txt = view.findViewById(R.id.boycotter_txt);
        TextView boycotter_long_txt = view.findViewById(R.id.boycotter_long_txt);

        shareBtn = view.findViewById(R.id.shareScore);
        shareBtn.setOnClickListener(this);

        SharedPreferences preferences = requireContext().getSharedPreferences(BOYCOTT_PREFERENCE_KEY, Context.MODE_PRIVATE);
        netScore = preferences.getInt(BOYCOTT_SCORE_KEY, 0);

        if (TOTAL_INSTALLED_CHINESE_APPS > 0) {
            if (netScore <= 0) {
                appreciation_text.setText(R.string.no_way);
                boycotter_txt.setText(R.string.sorry_to_say);
                boycotter_long_txt.setText(R.string.no_apps_installed);
            }
        } else {
            appreciation_text.setText(R.string._amazing);
            boycotter_long_txt.setText(R.string.wow);
        }

        score.setText(MessageFormat.format("{0}", (netScore * 100)));
        uninstallCount.setText(MessageFormat.format("{0}", netScore));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.shareScore)
            takeAndShareScreenshot();
    }

    private void takeAndShareScreenshot() {
        Bitmap ss = takeScreenshot();
        saveBitmap(ss);
        shareIt();
    }

    private Bitmap takeScreenshot() {
        if (shareBtn != null)
            shareBtn.setVisibility(View.GONE);
        View rootView = requireActivity().getWindow().getDecorView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = rootView.getDrawingCache();
        if (shareBtn != null)
            shareBtn.setVisibility(View.VISIBLE);
        return bitmap;
    }

    private void saveBitmap(Bitmap bitmap) {
        imagePath = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + "BoycottScore" + ".jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void shareIt() {
        try {

            Uri uri = FileProvider.getUriForFile(requireContext(), "com.japps.boycotter.fileprovider", imagePath);
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/*");
            String shareBody = getString(R.string.share_body_text);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.boycotter);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

            Intent chooserIntent = Intent.createChooser(sharingIntent, "Share Boycott score via");
            startActivity(chooserIntent);
        } catch (Exception e) {
            Log.e("Exception", "" + e.toString());
        }
    }
}