package org.phramusca.cookandfreeze;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.phramusca.cookandfreeze.database.HelperDb;
import org.phramusca.cookandfreeze.databinding.ActivityMainBinding;
import org.phramusca.cookandfreeze.helpers.HelperFile;
import org.phramusca.cookandfreeze.helpers.HelperToast;
import org.phramusca.cookandfreeze.ui.core.SectionsPagerAdapter;
import org.phramusca.cookandfreeze.ui.main.PlaceholderFragment;
import org.phramusca.cookandfreeze.ui.recipient.FragmentRecipient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final HelperToast helperToast = new HelperToast(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!HelperFile.init(this)) {
            helperToast.toastLong("Unable to find a writable application folder. Exiting :(");
            return;
        }
        checkPermissionsAndConnectDatabase();

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Map<String, Fragment> pages = new LinkedHashMap<>();
        pages.put(getResources().getString(R.string.tab_text_1), new FragmentRecipient(this));
        pages.put(getResources().getString(R.string.tab_text_2), PlaceholderFragment.newInstance(2));

        SectionsPagerAdapter sectionsPagerAdapter
                = new SectionsPagerAdapter(this, new ArrayList<>(pages.values()));
        ViewPager2 viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText((String) pages.keySet().toArray()[position])
        ).attach();
    }

    private final String[] PERMISSIONS = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
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