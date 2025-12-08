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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.database.HelperDb;
import org.phramusca.cookandfreeze.helpers.HelperDateTime;
import org.phramusca.cookandfreeze.models.Recipient;
import org.phramusca.cookandfreeze.ui.recipient.AdapterCursorRecipient;
import org.phramusca.cookandfreeze.ui.recipient.AdapterListItemRecipient;

import java.util.Calendar;
import java.util.Date;

public class FragmentInventory extends Fragment {

    private Context mContext;
    private TextView textTotalCount;
    private TextView textRecentCount;
    private TextView textOldestRecipient;
    private RecyclerView recyclerView;
    private TextView textEmptyInventory;
    private View cardOldest;
    private AdapterCursorRecipient adapterCursorRecipient;

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
        textOldestRecipient = view.findViewById(R.id.text_oldest_recipient);
        recyclerView = view.findViewById(R.id.recycler_view_inventory);
        textEmptyInventory = view.findViewById(R.id.text_empty_inventory);
        cardOldest = view.findViewById(R.id.card_oldest);

        updateStatistics();
        loadRecipients();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatistics();
        loadRecipients();
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
            Recipient oldestRecipient = null;
            Date oldestDate = new Date();

            if (cursor.moveToFirst()) {
                do {
                    AdapterListItemRecipient item = AdapterListItemRecipient.fromCursor(cursor);
                    Date recipientDate = item.getDate();

                    // Compter les récents
                    if (recipientDate.after(sevenDaysAgo)) {
                        recentCount++;
                    }

                    // Trouver le plus ancien
                    if (oldestRecipient == null || recipientDate.before(oldestDate)) {
                        oldestDate = recipientDate;
                        oldestRecipient = new Recipient(
                                item.getUuid(),
                                item.getTitle(),
                                item.getContent(),
                                item.getDate()
                        );
                    }
                } while (cursor.moveToNext());
            }

            textRecentCount.setText(String.valueOf(recentCount));

            // Afficher le plus ancien récipient
            if (oldestRecipient != null && totalCount > 0) {
                cardOldest.setVisibility(View.VISIBLE);
                String oldestText = oldestRecipient.getTitle();
                if (!oldestRecipient.getContent().isEmpty()) {
                    oldestText += " - " + oldestRecipient.getContent();
                }
                oldestText += "\n" + getString(R.string.date_frozen) + ": " +
                        HelperDateTime.formatUTC(oldestRecipient.getDate(),
                                HelperDateTime.DateTimeFormat.HUMAN_SIMPLE, true);
                textOldestRecipient.setText(oldestText);
            } else {
                cardOldest.setVisibility(View.GONE);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private void loadRecipients() {
        Cursor cursor = HelperDb.db.getRecipients("");
        if (cursor == null || cursor.getCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            textEmptyInventory.setVisibility(View.VISIBLE);
            return;
        }

        recyclerView.setVisibility(View.VISIBLE);
        textEmptyInventory.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapterCursorRecipient = new AdapterCursorRecipient(cursor);
        adapterCursorRecipient.addListener(adapterListItemRecipient -> {
            // Naviguer vers l'onglet récipients pour éditer
            if (getActivity() != null) {
                androidx.viewpager2.widget.ViewPager2 viewPager = getActivity().findViewById(R.id.view_pager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(0);
                    // Le récipient sera sélectionné dans le FragmentRecipient
                }
            }
        });
        recyclerView.setAdapter(adapterCursorRecipient);
    }
}

