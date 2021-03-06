package com.japps.boycotter.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.japps.boycotter.R;
import com.japps.boycotter.adapters.UninstalledAppAlternativeAdapter;
import com.japps.boycotter.ui.home.ChineseAppsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_FIRST_USER;
import static android.app.Activity.RESULT_OK;
import static com.japps.boycotter.MyApplication.BOYCOTT_PREFERENCE_KEY;
import static com.japps.boycotter.MyApplication.BOYCOTT_SCORE_KEY;
import static com.japps.boycotter.MyApplication.TOTAL_INSTALLED_CHINESE_APPS;
import static com.japps.boycotter.MyApplication.activeInternet;
import static com.japps.boycotter.MyApplication.baseURL;
import static com.japps.boycotter.MyApplication.getExecutor;

public class Uninstaller extends Fragment implements Response.ErrorListener, Response.Listener<JSONArray>, View.OnClickListener, UninstalledAppAlternativeAdapter.onInstallClickListener {
    public Uninstaller() {
    }

    private final int UNINSTALL_REQUEST_CODE = 1;
    private final String REQ_TAG = "alternative_app_request";
    private UninstalledAppAlternativeAdapter adapter;
    private RequestQueue requestQueue;
    private Button alertOkButton;

    private String appToUninstall;
    private int appPos;
    private Drawable uninstallingAppIcon = null;
    private String uninstallingAppName = "XYZ";

    private ProgressBar bar = null;
    private int progress;

    private ProgressBar contentLoadingBar;
    TextView txtNoInternet;

    private final String TOAST_SUCCESS = "success";
    private final String TOAST_FAIL = "fail";

    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> packages = new ArrayList<>();
    private ArrayList<String> stars = new ArrayList<>();
    private ArrayList<String> downloads = new ArrayList<>();
    private ArrayList<String> icons = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().setTitle("Boycott");
        final Bundle args = getArguments();

        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (args != null) {
                    appToUninstall = UninstallerArgs.fromBundle(args).getAppToUninstall();
                    appPos = UninstallerArgs.fromBundle(args).getAppPos();
                }

                if (requestQueue == null)
                    requestQueue = Volley.newRequestQueue(requireContext());

                String url = baseURL + appToUninstall;
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                        Uninstaller.this, Uninstaller.this);
                request.setTag(REQ_TAG);
                requestQueue.add(request);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_uninstaller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentLoadingBar = view.findViewById(R.id.alternative_loadingBar);

        TextView appName = view.findViewById(R.id.uninstalling_app_name);
        ImageView appIcon = view.findViewById(R.id.uninstalling_app_icon);

        Button appUninstallBtn = view.findViewById(R.id.uninstalling_app_btn);
        appUninstallBtn.setOnClickListener(this);

        PackageManager packageManager = requireActivity().getPackageManager();

        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(appToUninstall, 0);
            Resources resources = packageManager.getResourcesForApplication(applicationInfo);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                uninstallingAppIcon = resources.getDrawable(applicationInfo.icon, requireActivity().getTheme());
            } else
                uninstallingAppIcon = resources.getDrawable(applicationInfo.icon);
            uninstallingAppName = resources.getString(applicationInfo.labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        appName.setText(uninstallingAppName);
        appIcon.setImageDrawable(uninstallingAppIcon);

        txtNoInternet = view.findViewById(R.id.text_noInternet);

        if (!activeInternet){
            txtNoInternet.setVisibility(View.VISIBLE);
            contentLoadingBar.setVisibility(View.GONE);
        }else
            contentLoadingBar.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = view.findViewById(R.id.list_alternative_of_uninstalled);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());

        recyclerView.setLayoutManager(layoutManager);
        adapter = new UninstalledAppAlternativeAdapter(names, packages, stars, downloads, icons);
        recyclerView.setAdapter(adapter);

        adapter.setOnInstallClickListener(Uninstaller.this);
    }

    @Override
    public void installClickListener(String appToInstall) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String playStoreBaseURL = "https://play.google.com/store/apps/details?id=";
        intent.setData(Uri.parse(playStoreBaseURL + appToInstall));
        intent.setPackage("com.android.vending");
        startActivity(intent);
        Log.e("Install ", "" + appToInstall);
    }

    @Override
    public void onResponse(JSONArray response) {
        contentLoadingBar.setVisibility(View.GONE);
        if (response.length() != 0)
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);

                    String icon = object.has("icon") ? object.getString("icon") : "";
                    String title = object.has("title") ? object.getString("title") : "";
                    String packageId = object.has("appId") ? object.getString("appId") : "";
                    String installs = object.has("installs") ? object.getString("installs") : "";
                    String scoreText = object.has("scoreText") ? object.getString("scoreText") : "";

                    names.add(title);
                    icons.add(icon);
                    packages.add(packageId);
                    downloads.add(installs);
                    stars.add(scoreText);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        else
            customToast(getString(R.string.no_response), Toast.LENGTH_SHORT, TOAST_FAIL);
        Log.e("RESPONSE", " - " + response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        contentLoadingBar.setVisibility(View.GONE);
        Log.e("ERROR", " - " + error);

        if (error instanceof TimeoutError)
            customToast(getString(R.string.server_not_responding), Toast.LENGTH_SHORT, TOAST_FAIL);

        if (error.networkResponse != null) {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());

            if (data.equals("NOTFOUND")) {
                if (txtNoInternet != null)
                    txtNoInternet.setVisibility(View.GONE);
                ViewStub stub = requireView().findViewById(R.id.viewstub_no_alternative);
                stub.inflate();
            }

        }
    }

    private boolean warningDownloadSuggestedAppFirstShown;
    private AlertDialog dialog;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.uninstalling_app_btn) {
            if (!warningDownloadSuggestedAppFirstShown) {
                dialog = warnDownloadSuggestedAppFirst(requireContext());
                dialog.show();
                alertOkButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                alertOkButton.setEnabled(false);
                enableOkButton();   // enable after 5s
                warningDownloadSuggestedAppFirstShown = true;
                return;
            }

            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
            intent.setData(Uri.parse("package:" + appToUninstall));
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int netScore;
                customToast(getString(R.string.uninstall_success), Toast.LENGTH_SHORT, TOAST_SUCCESS);

                ChineseAppsViewModel viewModel = ChineseAppsViewModel.getInstance();
                // this removes app from the installed list of Chinese apps
                boolean removedFromList = viewModel.removeApp(appPos);
                Log.e("removedFromList", "" + removedFromList);

                TOTAL_INSTALLED_CHINESE_APPS -= 1;

                SharedPreferences preferences = requireContext().getSharedPreferences(BOYCOTT_PREFERENCE_KEY, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                netScore = preferences.getInt(BOYCOTT_SCORE_KEY, 0);
                editor.putInt(BOYCOTT_SCORE_KEY, (netScore + 1));
                editor.apply();

                Log.e("TAG", "onActivityResult: user accepted the (un)install");
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("TAG", "onActivityResult: user canceled the (un)install");
            } else if (resultCode == RESULT_FIRST_USER) {
                customToast(getString(R.string.uninstall_failed), Toast.LENGTH_SHORT, TOAST_FAIL);
                Log.e("TAG", "onActivityResult: failed to (un)install");
            }
        }
    }

    public AlertDialog warnDownloadSuggestedAppFirst(Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.install_alternatives)
                .setMessage(R.string.install_alternative_text)
                .setCancelable(false)
                .setView(R.layout.layout_download_laternative_dialog_progress)
                .setPositiveButton(R.string.alert_positive_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    private void enableOkButton() {
        final Handler msgPasser = new Handler();

        getExecutor().execute(new Runnable() {
            @Override
            public void run() {

                if (dialog != null)
                    bar = dialog.findViewById(R.id.dialog_alternative_progressbar);

                try {

                    for (progress = 0; progress < 100; progress++) {
                        if (bar != null) msgPasser.post(new Runnable() {
                            @Override
                            public void run() {
                                bar.setProgress(progress + 1);
                            }
                        });
                        Thread.sleep(50);
                    }

                    if (alertOkButton != null)
                        msgPasser.post(new Runnable() {
                            @Override
                            public void run() {
                                alertOkButton.setEnabled(true);
                            }
                        });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void customToast(@NonNull String text, int length, String type) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout;

        switch (type) {
            case TOAST_FAIL:
                layout = inflater.inflate(R.layout.layout_failed_toast, null, false);
                break;
            case TOAST_SUCCESS:
                layout = inflater.inflate(R.layout.layout_success_toast, null, false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        TextView message = layout.findViewById(R.id.success_text);
        message.setText(text);

        Toast toast = new Toast(requireContext().getApplicationContext());
        toast.setGravity(Gravity.TOP, 20, 0);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (requestQueue != null)
            requestQueue.cancelAll(REQ_TAG);

        if (dialog != null)
            dialog.dismiss();
    }
}