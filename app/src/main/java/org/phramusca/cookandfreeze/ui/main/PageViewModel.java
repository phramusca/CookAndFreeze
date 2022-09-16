package org.phramusca.cookandfreeze.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private final MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private final LiveData<String> mText =
            Transformations.map(mIndex, input -> "Tab #" + input + " is not yet available");

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
}