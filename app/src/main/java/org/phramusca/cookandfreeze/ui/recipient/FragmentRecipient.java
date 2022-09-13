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

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.database.HelperDb;
import org.phramusca.cookandfreeze.databinding.DialogModificationBinding;
import org.phramusca.cookandfreeze.helpers.HelperDateTime;
import org.phramusca.cookandfreeze.models.Recipient;
import org.phramusca.cookandfreeze.ui.main.CaptureActivityPortrait;

public class FragmentRecipient extends Fragment {

    private String searchQuery="";
    private Context mContext;

    // Register the launcher and result handler
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(mContext, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    String content = result.getContents();
                    promptRecipient(content);
                }
            });

    public FragmentRecipient(Context context) {
        mContext = context;
    }

    @SuppressLint("Range")
    private void promptRecipient(String uuid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
        AdapterCursorRecipient adapterCursorRecipient = new AdapterCursorRecipient(mContext, cursor);
        recyclerView.setAdapter(adapterCursorRecipient);
        //TODO: prompt modification
//        adapterCursorAlbum.addListener(adapterListItemAlbum -> {
////            //Open album tracks layout
////            Intent intent = new Intent(mContext, ActivityAlbumTracks.class);
////            intent.putExtra("idPath", adapterListItemAlbum.getIdPath()); //NON-NLS
////            intent.putExtra("searchQuery", searchQuery);
////            startActivityForResult(intent, ALBUM_TRACK_REQUEST_CODE);
//        });

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
}