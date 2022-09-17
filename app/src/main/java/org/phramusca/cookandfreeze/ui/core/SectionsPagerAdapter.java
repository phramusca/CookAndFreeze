package org.phramusca.cookandfreeze.ui.core;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class SectionsPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> mContent;

    public SectionsPagerAdapter(FragmentActivity fragmentActivity, List<Fragment> content) {
        super(fragmentActivity);
        mContent = content;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mContent.get(position);
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }
}