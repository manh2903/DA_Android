package com.ndm.da_test.ViewPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.ndm.da_test.Fragment.FireFragment;
import com.ndm.da_test.Fragment.HomeFragment;
import com.ndm.da_test.Fragment.MyPageFragment;
import com.ndm.da_test.Fragment.SkillFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SkillFragment();
            case 2:
                return new FireFragment();
            case 3:
                return new MyPageFragment();
            default:
                return new HomeFragment();
        }
    }
    @Override
    public int getCount() {
        return 4;
    }
}
