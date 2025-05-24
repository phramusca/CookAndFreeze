package org.phramusca.cookandfreeze.ui.recipient;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.database.HelperDb;
import org.phramusca.cookandfreeze.helpers.HelperDateTime;
import org.phramusca.cookandfreeze.ui.core.AdapterCursor;
import org.phramusca.cookandfreeze.ui.core.AdapterLoad;

import java.util.ArrayList;

public class AdapterCursorRecipient extends AdapterCursor<AdapterLoad.UserViewHolder> implements Filterable {

    public AdapterCursorRecipient(Cursor cursor) {
        super(cursor);
        oriCursor = cursor;
    }

    @Override
    @NotNull
    public AdapterLoad.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item_recipient, parent, false);
        return new AdapterLoad.UserViewHolder(itemView);
    }

    public AdapterListItemRecipient getRecipientListItem(int position) {
        AdapterListItemRecipient adapterListItemRecipient = null;
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            adapterListItemRecipient = AdapterListItemRecipient.fromCursor(cursor);
        }
        return adapterListItemRecipient;
    }

    @Override
    public void onBindViewHolder(AdapterLoad.UserViewHolder userViewHolder, Cursor cursor, int position) {
        AdapterListItemRecipient adapterListItemRecipient = AdapterListItemRecipient.fromCursor(cursor);

        userViewHolder.item_line1.setText(adapterListItemRecipient.getTitle());

        String content = adapterListItemRecipient.getContent();
        if (!searchQuery.isEmpty()) {
            String lowerContent = content.toLowerCase();
            String lowerQuery = searchQuery.toLowerCase();
            int start = lowerContent.indexOf(lowerQuery);
            if (start >= 0) {
                android.text.SpannableString spannable = new android.text.SpannableString(content);
                int end = start + lowerQuery.length();
                int color = ContextCompat.getColor(
                    userViewHolder.item_line2.getContext(),
                    R.color.teal_700
                );
                spannable.setSpan(
                    new android.text.style.ForegroundColorSpan(color),
                    start, end,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                userViewHolder.item_line2.setText(spannable);
            } else {
                userViewHolder.item_line2.setText(content);
            }
        } else {
            userViewHolder.item_line2.setText(content);
        }

        String dateDisplay = HelperDateTime.formatUTC(adapterListItemRecipient.getDate(),
                HelperDateTime.DateTimeFormat.HUMAN_SIMPLE, true);
        userViewHolder.item_line3.setText(dateDisplay);

        userViewHolder.itemView.setOnClickListener(view -> sendListener(adapterListItemRecipient));
    }

    private final ArrayList<IListenerAdapterRecipient> mListListener = new ArrayList<>();

    public void addListener(IListenerAdapterRecipient aListener) {
        mListListener.add(aListener);
    }

    void sendListener(AdapterListItemRecipient adapterListItemRecipient) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClick(adapterListItemRecipient);
        }
    }

    private final Cursor oriCursor;
    private String searchQuery="";

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Cursor cursor;
            if (constraint != null && constraint.length() != 0) {
                 cursor = HelperDb.db.getRecipients(constraint.toString().toLowerCase().trim());
            } else {
                cursor = oriCursor;
            }
            FilterResults results = new FilterResults();
            results.values = cursor;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Cursor cursor = (Cursor) results.values;
            if(cursor!=null) {
                searchQuery = constraint.toString().toLowerCase().trim();
                swapCursor(cursor);
            }
        }
    };
}