package org.phramusca.cookandfreeze;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.phramusca.cookandfreeze.database.HelperDb;
import org.phramusca.cookandfreeze.databinding.ActivityMainBinding;
import org.phramusca.cookandfreeze.databinding.DialogModificationBinding;
import org.phramusca.cookandfreeze.models.Recipient;
import org.phramusca.cookandfreeze.helpers.HelperDateTime;
import org.phramusca.cookandfreeze.ui.main.CaptureActivityPortrait;
import org.phramusca.cookandfreeze.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    //TODO: Use ScanContract instead: https://github.com/journeyapps/zxing-android-embedded
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissionsAndConnectDatabase();

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter
                = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = binding.fab;

        qrScan = new IntentIntegrator(this);
        qrScan.setPrompt("Scan a barcode on a recipient.");
        //qrScan.setCameraId(0);  // Use a specific camera of the device
        qrScan.setOrientationLocked(true);
        qrScan.setBeepEnabled(false);
        qrScan.setCaptureActivity(CaptureActivityPortrait.class);

        fab.setOnClickListener(view -> {
            qrScan.initiateScan();
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //        .setAction("Action", null).show();
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                String content = result.getContents();
                promptRecipient(content);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @SuppressLint("Range")
    private void promptRecipient(String uuid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_modification, null);
        DialogModificationBinding dialogModificationBinding = DialogModificationBinding.bind(view);

        Recipient recipient = HelperDb.db.getRecipient(uuid);
        dialogModificationBinding.number.setText(String.valueOf(recipient.getNumber()));
        dialogModificationBinding.content.setText(recipient.getContent());
        dialogModificationBinding.date.setText(HelperDateTime.formatUTC(recipient.getDate(), HelperDateTime.DateTimeFormat.HUMAN, true));

        builder
                .setView(view)
                .setPositiveButton("Modifier",
                        (dialog, id) -> HelperDb.db.insertOrUpdateRecipient(
                            Integer.parseInt(dialogModificationBinding.number.getText().toString()),
                                uuid,
                            dialogModificationBinding.content.getText().toString()))
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
                .create()
                .show();
    }

    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int REQUEST = 15694;

    public void checkPermissionsAndConnectDatabase() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST);
        } else {
            connectDatabase();
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context != null
                && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            connectDatabase();
        }
    }

    private void connectDatabase() {
        HelperDb.open(this);
    }
}