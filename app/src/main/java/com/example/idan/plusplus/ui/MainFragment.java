package com.example.idan.plusplus.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;

import com.example.idan.plusplus.BuildConfig;
import com.example.idan.plusplus.Dialogs.AppUpdateDialogActivity;
import com.example.idan.plusplus.R;
import com.example.idan.plusplus.Utils;
import com.example.idan.plusplus.V2.Tasks.GetUpdateAppAsyncTask;
import com.example.idan.plusplus.model.GridItem;
import com.example.idan.plusplus.presenter.GridCardPresenter;
import com.example.idan.plusplus.presenter.IconHeaderItemPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class MainFragment extends BrowseSupportFragment {
    private static final int PERMISSION_REQUEST_WRITE_STORAGE = 0;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1;

    private ArrayObjectAdapter mCategoryRowAdapter;
    private BackgroundManager mBackgroundManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_STORAGE);
            }
        }

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        setupLeanbackElements();

        setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {
            if (item instanceof GridItem) {
                Utils.gridItemClicked(getActivity(), R.id.main_frame, (GridItem)item);
            }
        });

        prepareEntranceTransition();
        loadData();
        startEntranceTransition();
        // showStartupMessage();
        // startUpdateTask();
    }

    @Override
    public void onStart() {
        Utils.disposedServices();
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mBackgroundManager != null)
            mBackgroundManager.release();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mBackgroundManager = null;
        Utils.disposedServices();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), R.string.permission_denied_write_storage, Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        }
    }

    private void setupLeanbackElements() {
        setBadgeDrawable(getActivity().getDrawable(R.drawable.logo_new));
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(false);
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));
        setHeaderPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object o) {
                return new IconHeaderItemPresenter();
            }
        });
    }

    private void loadData() {
        ListRowPresenter listRowPresenter = new ListRowPresenter();
        mCategoryRowAdapter = new ArrayObjectAdapter(listRowPresenter);
        mCategoryRowAdapter.clear();

        loadStaticIsraelLiveChannelData();
        loadStaticIsraelRadioChannelData();

        setAdapter(mCategoryRowAdapter);
    }

    private void showStartupMessage() {
        switch (Utils.checkAppStart(getContext())) {
            case NORMAL:
                break;
            case FIRST_TIME_VERSION:
                showDialog( getString(R.string.WHAT_IS_NEW_TXT) +
                        getString(R.string.ENGLISH_DISCLAMIER) +
                        getString(R.string.HEBREW_DISCLAMIER));
                break;
            case FIRST_TIME:
                showDialog(getString(R.string.ENGLISH_DISCLAMIER) +
                        getString(R.string.HEBREW_DISCLAMIER));
                break;
            default:
                break;
        }
    }

    private void startUpdateTask()  {
        GetUpdateAppAsyncTask updateAppAsyncTask = new GetUpdateAppAsyncTask(getActivity(), R.id.main_frame, (localVer, serverVer, file) -> {
            if (localVer > 0 && serverVer > 0 && file != null) {
                Intent intent = new Intent(getActivity(), AppUpdateDialogActivity.class);
                intent.putExtra("NewVer", serverVer);
                intent.putExtra("OldVer", localVer);
                intent.putExtra("urlToUpdateFile",file);
                startActivity(intent);
            } else if (localVer != -1 && serverVer != -1 && file != null) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setTitle(Objects.requireNonNull(getActivity()).getString(R.string.UPDAE_ERROR_MSESSAGE_TITLE));
                builder1.setNeutralButton(getActivity().getString(R.string.BTN_GOTIT), (dialogInterface, i) -> dialogInterface.dismiss());
                builder1.setMessage(getActivity().getString(R.string.UPDAE_ERROR_MSESSAGE_TITLE));
                AlertDialog alert11 = builder1.create();
                alert11.setOnCancelListener(dialogInterface -> {
                    getActivity().finish();
                });
                alert11.setOnDismissListener(dialogInterface -> {
                    getActivity().finish();
                });
                alert11.show();
            }
        });

        if (!BuildConfig.DEBUG) {
            updateAppAsyncTask.execute();
        }
    }

    private void loadStaticIsraelLiveChannelData() {
        List<GridItem> list = Utils.getStaticIsraelLiveChannelsData(getContext());
        GridCardPresenter gridCardPresenter = new GridCardPresenter(this);
        HeaderItem header = new HeaderItem(1,getString(R.string.ISRAEL_LIVE_CHANNELS_CATEGORY));
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(gridCardPresenter);
        listRowAdapter.addAll(0,list);
        mCategoryRowAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void loadStaticIsraelRadioChannelData() {
        List<GridItem> list = Utils.getStaticIsraelRadioData(getContext());
        GridCardPresenter gridCardPresenter = new GridCardPresenter(this);
        HeaderItem header = new HeaderItem(0,getString(R.string.ISRAEL_RADIO_CHANNELS_CATEGORY));
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(gridCardPresenter);
        listRowAdapter.addAll(0,list);
        mCategoryRowAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(Html.fromHtml(message));
        builder.setCancelable(true);
        builder.setPositiveButton(
                R.string.BTN_GOTIT,
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
        ((TextView)alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }
}