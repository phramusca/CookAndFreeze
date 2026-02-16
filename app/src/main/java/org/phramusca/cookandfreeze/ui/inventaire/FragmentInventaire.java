package org.phramusca.cookandfreeze.ui.inventaire;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.database.HelperDb;
import org.phramusca.cookandfreeze.models.QRCodeV1;
import org.phramusca.cookandfreeze.ui.core.CaptureActivityPortrait;
import org.phramusca.cookandfreeze.ui.recipient.AdapterListItemRecipient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Fragment « Scanne-les tous » : l’utilisateur scanne chaque récipient pour vider la liste. */
public class FragmentInventaire extends Fragment {

    private Context mContext;
    private TextView textRemaining;
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    private AdapterInventaire adapter;
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;

    public FragmentInventaire(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() == null) return;
            String content = result.getContents();
            if (!content.startsWith("cookandfreeze://")) {
                Toast.makeText(mContext, getString(R.string.scan) + " : QR invalide", Toast.LENGTH_SHORT).show();
                return;
            }
            content = content.substring("cookandfreeze://".length());
            try {
                Gson gson = new Gson();
                Map<?, ?> map = gson.fromJson(content, Map.class);
                if (map == null || !map.containsKey("version")) return;
                double version = ((Number) map.get("version")).doubleValue();
                if (version != 1) return;
                QRCodeV1 qrCodeV1 = gson.fromJson(content, QRCodeV1.class);
                if (qrCodeV1 == null || qrCodeV1.uuid == null) return;
                onRecipientScanned(qrCodeV1.uuid);
            } catch (JsonSyntaxException ignored) {
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_inventaire, container, false);

        textRemaining = view.findViewById(R.id.text_inventaire_remaining);
        recyclerView = view.findViewById(R.id.recycler_view_inventaire);
        layoutEmpty = view.findViewById(R.id.layout_inventaire_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new AdapterInventaire();
        recyclerView.setAdapter(adapter);

        loadList();

        ExtendedFloatingActionButton buttonScan = view.findViewById(R.id.button_scan_inventaire);
        buttonScan.setOnClickListener(v -> {
            ScanOptions scanOptions = new ScanOptions()
                    .setPrompt(getString(R.string.scan))
                    .setOrientationLocked(true)
                    .setBeepEnabled(false)
                    .setCaptureActivity(CaptureActivityPortrait.class);
            barcodeLauncher.launch(scanOptions);
        });

        return view;
    }

    /** Charge la liste une seule fois à l’ouverture ; elle ne se recharge pas en changeant d’onglet. */
    private void loadList() {
        Cursor cursor = HelperDb.db.getRecipients("");
        if (cursor == null) {
            updateUiFromCount(0);
            return;
        }
        List<AdapterListItemRecipient> list = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    list.add(AdapterListItemRecipient.fromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        adapter.setItems(list);
        updateUiFromCount(list.size());
    }

    private void onRecipientScanned(String uuid) {
        if (adapter.removeByUuid(uuid)) {
            Toast.makeText(mContext, getString(R.string.inventaire_scanned_toast), Toast.LENGTH_SHORT).show();
            updateUiFromCount(adapter.getRemainingCount());
        }
    }

    private void updateUiFromCount(int remaining) {
        textRemaining.setText(getString(R.string.inventaire_remaining, remaining));
        if (remaining == 0) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}
