package org.phramusca.cookandfreeze.ui.inventaire;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.helpers.HelperDateTime;
import org.phramusca.cookandfreeze.ui.core.AdapterLoad;
import org.phramusca.cookandfreeze.ui.recipient.AdapterListItemRecipient;
import org.phramusca.cookandfreeze.ui.recipient.IListenerAdapterRecipient;

import java.util.ArrayList;
import java.util.List;

/** Adapteur pour la liste « Scanne-les tous » : même affichage que Récipients, clic ouvre la popup de modification. */
public class AdapterInventaire extends RecyclerView.Adapter<AdapterLoad.UserViewHolder> {

    private final List<AdapterListItemRecipient> items = new ArrayList<>();
    private final ArrayList<IListenerAdapterRecipient> listeners = new ArrayList<>();

    public void addListener(IListenerAdapterRecipient listener) {
        listeners.add(listener);
    }

    public void setItems(List<AdapterListItemRecipient> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    /** Met à jour l’item correspondant à l’uuid (après édition dans la popup). */
    public void updateItemByUuid(String uuid, AdapterListItemRecipient updated) {
        for (int i = 0; i < items.size(); i++) {
            if (uuid.equals(items.get(i).getUuid())) {
                items.set(i, updated);
                notifyItemChanged(i);
                return;
            }
        }
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
        if (item.getInventoryDate() != null && holder.item_line4 != null) {
            holder.item_line4.setVisibility(View.VISIBLE);
            holder.item_line4.setText(holder.item_line4.getContext().getString(R.string.date_inventory) + ": " +
                    HelperDateTime.formatUTC(item.getInventoryDate(), HelperDateTime.DateTimeFormat.HUMAN_SIMPLE, true));
        } else if (holder.item_line4 != null) {
            holder.item_line4.setVisibility(View.GONE);
        }

        View clickTarget = holder.itemView.findViewById(R.id.layout_item);
        (clickTarget != null ? clickTarget : holder.itemView).setOnClickListener(v -> {
            for (int i = listeners.size() - 1; i >= 0; i--) {
                listeners.get(i).onClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
