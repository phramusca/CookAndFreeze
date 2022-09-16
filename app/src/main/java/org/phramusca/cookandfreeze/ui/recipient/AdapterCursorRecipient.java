package org.phramusca.cookandfreeze.ui.recipient;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import org.jetbrains.annotations.NotNull;
import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.database.HelperDb;
import org.phramusca.cookandfreeze.ui.core.AdapterCursor;
import org.phramusca.cookandfreeze.ui.core.AdapterLoad;

import java.util.ArrayList;

public class AdapterCursorRecipient extends AdapterCursor<AdapterLoad.UserViewHolder> implements Filterable {

    private ViewGroup parent;

    public AdapterCursorRecipient(Context context, Cursor cursor) {
        super(context, cursor);
        oriCursor = cursor;
    }

    @Override
    @NotNull
    public AdapterLoad.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
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

        userViewHolder.item_line1.setText(String.valueOf(adapterListItemRecipient.getNumber()));
        if(!searchQuery.isEmpty()) {
            userViewHolder.item_line1.setTextToHighlight(searchQuery);
            userViewHolder.item_line1.setTextHighlightColor(R.color.teal_700);
            userViewHolder.item_line1.setCaseInsensitive(true);
            userViewHolder.item_line1.highlight();
        }

        userViewHolder.item_line2.setText(adapterListItemRecipient.getContent());
        if(!searchQuery.isEmpty()) {
            userViewHolder.item_line2.setTextToHighlight(searchQuery);
            userViewHolder.item_line2.setTextHighlightColor(R.color.teal_700);
            userViewHolder.item_line2.setCaseInsensitive(true);
            userViewHolder.item_line2.highlight();
        }

        userViewHolder.item_line3.setText(adapterListItemRecipient.getDate());

        userViewHolder.item_line4.setText(adapterListItemRecipient.getUuid());

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