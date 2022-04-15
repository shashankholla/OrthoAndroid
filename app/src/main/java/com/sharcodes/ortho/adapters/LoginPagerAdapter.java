package com.sharcodes.ortho.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sharcodes.ortho.fragments.LoginFragment;
import com.sharcodes.ortho.fragments.SignUpFragment;

public class LoginPagerAdapter extends FragmentStatePagerAdapter {

    private final int noOfTabs;

    public LoginPagerAdapter(FragmentManager fm, int tabs) {
        super(fm);
        this.noOfTabs = tabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LoginFragment login = new LoginFragment();
                return login;
            case 1:
                SignUpFragment signUpFragment = new SignUpFragment();
                return signUpFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
