package com.android.webag.webag.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * created 2017 tongcai. Version V1.0
 * author yunfengl
 * date  2017-2-4下午3:47:25
 * Email liuyunfeng-1231@163.com
 * todo: 基础fragment
 *
 */
public abstract class BaseFragment extends Fragment implements View.OnTouchListener{

    public abstract void onRefresh();

    public abstract void onRefreshComplete();

    protected WeakReference<View> mRootView;
    LayoutInflater inflater;
    public boolean isfirst = true;

    public Dialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     *  返回
     * @param
     * @return void
     * @Exception:
     */
    public void goBack() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            getActivity().finish();
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

}
