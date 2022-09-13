package org.phramusca.cookandfreeze.ui.recipient;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.database.HelperDb;

public class FirstFragment extends Fragment {

    public FirstFragment() {
// Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_albums, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Cursor cursor = HelperDb.db.getRecipients();
        AdapterCursorRecipient adapterCursorAlbum = new AdapterCursorRecipient(getContext(), cursor);
        recyclerView.setAdapter(adapterCursorAlbum);
        //TODO: prompt modification
//        adapterCursorAlbum.addListener(adapterListItemAlbum -> {
//            //Open album tracks layout
//            Intent intent = new Intent(getApplicationContext(), ActivityAlbumTracks.class);
//            intent.putExtra("idPath", adapterListItemAlbum.getIdPath()); //NON-NLS
//            intent.putExtra("searchQuery", searchQuery);
//            startActivityForResult(intent, ALBUM_TRACK_REQUEST_CODE);
//        });

        return view;
    }
}