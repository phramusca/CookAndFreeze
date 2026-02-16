package org.phramusca.cookandfreeze.ui.inventaire;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.helpers.HelperDateTime;
import org.phramusca.cookandfreeze.ui.core.AdapterLoad;
import org.phramusca.cookandfreeze.ui.recipient.AdapterListItemRecipient;

import java.util.ArrayList;
import java.util.List;

/** Adapteur pour la liste « Scanne-les tous » : affiche les récipients restants à scanner. */
public class AdapterInventaire extends RecyclerView.Adapter<AdapterLoad.UserViewHolder> {

    private final List<AdapterListItemRecipient> items = new ArrayList<>();

    public void setItems(List<AdapterListItemRecipient> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    /** Retire le récipient avec cet uuid de la liste. Retourne true si trouvé et retiré. */
    public boolean removeByUuid(String uuid) {
        for (int i = 0; i < items.size(); i++) {
            if (uuid.equals(items.get(i).getUuid())) {
                items.remove(i);
                notifyItemRemoved(i);
                return true;
            }
        }
        return false;
    }

    public int getRemainingCount() {
        return items.size();
    }

    @NonNull
    @Override
    public AdapterLoad.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item_recipient, parent, false);
        return new AdapterLoad.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLoad.UserViewHolder holder, int position) {
        AdapterListItemRecipient item = items.get(position);
        holder.item_line1.setText(item.getTitle());
        holder.item_line2.setText(item.getContent());
        holder.item_line3.setText(HelperDateTime.formatUTC(item.getDate(),
                HelperDateTime.DateTimeFormat.HUMAN_SIMPLE, true));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
