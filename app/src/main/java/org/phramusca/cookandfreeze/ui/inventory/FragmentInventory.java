package org.phramusca.cookandfreeze.ui.inventory;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.database.HelperDb;
import org.phramusca.cookandfreeze.ui.recipient.AdapterListItemRecipient;

import java.util.Calendar;
import java.util.Date;

public class FragmentInventory extends Fragment {

    private Context mContext;
    private TextView textTotalCount;
    private TextView textRecentCount;

    public FragmentInventory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_inventory, container, false);

        textTotalCount = view.findViewById(R.id.text_total_count);
        textRecentCount = view.findViewById(R.id.text_recent_count);

        updateStatistics();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatistics();
    }

    private void updateStatistics() {
        Cursor cursor = HelperDb.db.getRecipients("");
        try {
            if (cursor == null) {
                textTotalCount.setText("0");
                textRecentCount.setText("0");
                return;
            }

            int totalCount = cursor.getCount();
            textTotalCount.setText(String.valueOf(totalCount));

            // Compter les récipients des 7 derniers jours
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            Date sevenDaysAgo = calendar.getTime();

            int recentCount = 0;

            if (cursor.moveToFirst()) {
                do {
                    AdapterListItemRecipient item = AdapterListItemRecipient.fromCursor(cursor);
                    Date recipientDate = item.getDate();
                    if (recipientDate.after(sevenDaysAgo)) {
                        recentCount++;
                    }
                } while (cursor.moveToNext());
            }

            textRecentCount.setText(String.valueOf(recentCount));
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }
}

