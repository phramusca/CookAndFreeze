package org.phramusca.cookandfreeze;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.phramusca.cookandfreeze.databinding.ActivityMainBinding;
import org.phramusca.cookandfreeze.databinding.DialogSigninBinding;
import org.phramusca.cookandfreeze.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        qrScan = new IntentIntegrator(this);

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
                promptInfo(content);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void promptInfo(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_signin, null);
        DialogSigninBinding dialogSigninBinding = DialogSigninBinding.bind(view);
        dialogSigninBinding.title.setText(content);
        builder
                .setView(view)
                .setPositiveButton("Positive", (dialog, id) -> {
                    Toast.makeText(getApplicationContext(), "Username: " + dialogSigninBinding.username.getText() + ", password: " + dialogSigninBinding.password.getText(), Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Negative", (dialog, id) -> {
                    dialog.cancel();
                })
                .create()
                .show();
    }
}