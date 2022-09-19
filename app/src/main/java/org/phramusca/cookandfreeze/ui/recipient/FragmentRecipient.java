package org.phramusca.cookandfreeze.ui.recipient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.database.HelperDb;
import org.phramusca.cookandfreeze.databinding.DialogModificationBinding;
import org.phramusca.cookandfreeze.helpers.HelperDateTime;
import org.phramusca.cookandfreeze.models.QRCodeV1;
import org.phramusca.cookandfreeze.models.Recipient;
import org.phramusca.cookandfreeze.ui.core.CaptureActivityPortrait;

import java.util.Date;
import java.util.Map;

public class FragmentRecipient extends Fragment {

    private String searchQuery="";
    private Context mContext;
    private AdapterCursorRecipient adapterCursorRecipient;

    public FragmentRecipient(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_recipients, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        Cursor cursor = HelperDb.db.getRecipients("");
        adapterCursorRecipient = new AdapterCursorRecipient(cursor);
        recyclerView.setAdapter(adapterCursorRecipient);
        adapterCursorRecipient.addListener(
                adapterListItemRecipient -> promptRecipient(adapterListItemRecipient.toRecipient()));

        EditText queryText = view.findViewById(R.id.filter_album);
        queryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                adapterCursorRecipient.getFilter().filter(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        queryText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.setVisibility(View.GONE);
                return true;
            }
            return false;
        });

        Button button_search = view.findViewById(R.id.button_search);
        button_search.setOnClickListener(v -> {
            queryText.setVisibility(View.VISIBLE);
            queryText.requestFocus();
            final InputMethodManager inputMethodManager = (InputMethodManager) v.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(queryText, InputMethodManager.SHOW_IMPLICIT);
        });

        Button button_scan = view.findViewById(R.id.button_scan);
        button_scan.setOnClickListener(v -> {
            ScanOptions scanOptions = new ScanOptions()
                    .setPrompt("Scan a barcode on a recipient.")
                    .setOrientationLocked(true)
                    .setBeepEnabled(false)
                    .setCaptureActivity(CaptureActivityPortrait.class);
            barcodeLauncher.launch(scanOptions);
        });
        return view;
    }

    //TODO: QR code to open application when read from third party barcode scanner
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(mContext, "Cancelled", Toast.LENGTH_LONG).show();
                    return;
                }
                String content = result.getContents();
                if(!content.startsWith("cookandfreeze://")) {
                    Toast.makeText(mContext, "Not a valid QR code: \n" + content, Toast.LENGTH_LONG).show();
                    return;
                }
                content = content.substring("cookandfreeze://".length());
                try {
                    Gson gson = new Gson();
                    Map<?, ?> map = gson.fromJson(content, Map.class);
                    if(!map.containsKey("version")) {
                        Toast.makeText(mContext, "Not a valid QR code: \n" + content, Toast.LENGTH_LONG).show();
                        return;
                    }
                    double version = (double) map.get("version");
                    if (version != 1) {
                        Toast.makeText(mContext, "Unsupported label version " + version + ". Please update.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    QRCodeV1 qrCodeV1 = gson.fromJson(content, QRCodeV1.class);
                    Recipient recipient = HelperDb.db.getRecipient(qrCodeV1.uuid);
                    if (recipient!=null) {
                        promptRecipient(recipient);
                    } else {
                        promptRecipient(qrCodeV1.toRecipient());
                    }
                } catch (JsonSyntaxException ex) {
                    Toast.makeText(mContext, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });

    @SuppressLint({"Range", "NotifyDataSetChanged"})
    private void promptRecipient(Recipient recipient) {
        Recipient originalRecipient = new Recipient(recipient.getUuid());
        try {
            originalRecipient = (Recipient) recipient.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        Recipient finalOriginalRecipient = originalRecipient;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = getLayoutInflater().inflate(R.layout.dialog_modification, null);
        DialogModificationBinding dialogModificationBinding = DialogModificationBinding.bind(view);

        dialogModificationBinding.title.setText(recipient.getTitle());
        dialogModificationBinding.content.setText(recipient.getContent());
        dialogModificationBinding.date.setText(HelperDateTime.formatUTC(recipient.getDate(), HelperDateTime.DateTimeFormat.HUMAN_SIMPLE, true));

        dialogModificationBinding.buttonClear.setOnClickListener(v -> dialogModificationBinding.content.setText(""));

        dialogModificationBinding.buttonDateNow.setOnClickListener(v -> {
            recipient.setDate(new Date());
            dialogModificationBinding.date.setText(HelperDateTime.formatUTC(recipient.getDate(), HelperDateTime.DateTimeFormat.HUMAN_SIMPLE, true));
        });

        dialogModificationBinding.buttonReset.setOnClickListener(v -> {
            recipient.setDate(finalOriginalRecipient.getDate());
            recipient.setContent(finalOriginalRecipient.getContent());
            recipient.setTitle(finalOriginalRecipient.getTitle());

            dialogModificationBinding.title.setText(recipient.getTitle());
            dialogModificationBinding.content.setText(recipient.getContent());
            dialogModificationBinding.date.setText(HelperDateTime.formatUTC(recipient.getDate(), HelperDateTime.DateTimeFormat.HUMAN_SIMPLE, true));
        });

        builder
                .setView(view)
                .setPositiveButton("Modify",
                        (dialog, id) -> {
                            HelperDb.db.insertOrUpdateRecipient(
                                    dialogModificationBinding.title.getText().toString(),
                                    recipient.getUuid(),
                                    dialogModificationBinding.content.getText().toString(),
                                    recipient.getDate());
                            Cursor cursor = HelperDb.db.getRecipients("");
                            adapterCursorRecipient.swapCursor(cursor);
                        })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
                .create()
                .show();
    }
}