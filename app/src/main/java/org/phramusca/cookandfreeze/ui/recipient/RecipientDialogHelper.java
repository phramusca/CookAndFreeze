package org.phramusca.cookandfreeze.ui.recipient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.database.HelperDb;
import org.phramusca.cookandfreeze.databinding.DialogModificationBinding;
import org.phramusca.cookandfreeze.helpers.HelperDateTime;
import org.phramusca.cookandfreeze.models.Recipient;

import java.util.Date;

/** Affiche la popup de modification/ajout/validation d’un récipient avec libellé de bouton selon le contexte. */
public final class RecipientDialogHelper {

    public enum Mode { ADD, EDIT, VALIDATE }

    /** Affiche le dialogue. positiveLabelId = R.string.action_add / modify / action_validate. */
    @SuppressLint("NotifyDataSetChanged")
    public static void show(
            Context context,
            LayoutInflater inflater,
            Recipient recipient,
            Mode mode,
            Runnable onRefreshList,
            Runnable onValidated,
            Runnable onDeleted) {

        Recipient originalRecipient = new Recipient(recipient.getUuid());
        try {
            originalRecipient = (Recipient) recipient.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        Recipient finalOriginalRecipient = originalRecipient;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = inflater.inflate(R.layout.dialog_modification, null);
        DialogModificationBinding b = DialogModificationBinding.bind(view);

        b.title.setText(recipient.getTitle());
        b.content.setText(recipient.getContent());
        b.date.setText(HelperDateTime.formatUTC(recipient.getDate(), HelperDateTime.DateTimeFormat.HUMAN_SIMPLE, true));

        if (recipient.getInventoryDate() != null) {
            b.inventoryDate.setVisibility(View.VISIBLE);
            b.inventoryDate.setText(context.getString(R.string.date_inventory) + ": " +
                    HelperDateTime.formatUTC(recipient.getInventoryDate(), HelperDateTime.DateTimeFormat.HUMAN_SIMPLE, true));
        } else {
            b.inventoryDate.setVisibility(View.GONE);
        }

        b.buttonClear.setOnClickListener(v -> b.content.setText(""));
        b.buttonDateNow.setOnClickListener(v -> {
            recipient.setDate(new Date());
            b.date.setText(HelperDateTime.formatUTC(recipient.getDate(), HelperDateTime.DateTimeFormat.HUMAN_SIMPLE, true));
        });
        b.buttonReset.setOnClickListener(v -> {
            recipient.setDate(finalOriginalRecipient.getDate());
            recipient.setContent(finalOriginalRecipient.getContent());
            recipient.setTitle(finalOriginalRecipient.getTitle());
            b.title.setText(recipient.getTitle());
            b.content.setText(recipient.getContent());
            b.date.setText(HelperDateTime.formatUTC(recipient.getDate(), HelperDateTime.DateTimeFormat.HUMAN_SIMPLE, true));
        });

        int positiveLabelId;
        switch (mode) {
            case ADD:
                positiveLabelId = R.string.action_add;
                break;
            case VALIDATE:
                positiveLabelId = R.string.action_validate;
                break;
            case EDIT:
            default:
                positiveLabelId = R.string.modify;
                break;
        }

        if (mode == Mode.ADD) {
            builder.setTitle(R.string.dialog_title_new_recipient);
        }

        builder.setView(view);

        builder.setPositiveButton(context.getString(positiveLabelId), (dialog, id) -> {
            if (mode == Mode.VALIDATE) {
                HelperDb.db.updateInventoryDate(recipient.getUuid(), new Date());
                if (onValidated != null) onValidated.run();
            } else {
                HelperDb.db.insertOrUpdateRecipient(
                        b.title.getText().toString(),
                        recipient.getUuid(),
                        b.content.getText().toString(),
                        recipient.getDate(),
                        mode == Mode.EDIT ? recipient.getInventoryDate() : null);
                if (onRefreshList != null) onRefreshList.run();
            }
        });

        builder.setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();

        b.buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.delete_confirm_title)
                    .setMessage(R.string.delete_confirm_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete, (d2, id2) -> {
                        HelperDb.db.deleteRecipient(recipient.getUuid());
                        dialog.dismiss();
                        if (onRefreshList != null) onRefreshList.run();
                        if (onDeleted != null) onDeleted.run();
                    })
                    .show();
        });

        // En mode VALIDATE on peut masquer le bouton Supprimer si tu préfères ; ici on le garde
        dialog.show();
    }
}
