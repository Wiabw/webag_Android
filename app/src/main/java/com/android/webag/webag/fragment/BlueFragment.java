package com.android.webag.webag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.webag.webag.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Date:2017/3/7 10:02
 * Author: Yangkaisheng
 * Email: kaishengy@51tongcai.com
 * Description: 找回密码第一页
 */
@ContentView(R.layout.fragment_bule)
public class BlueFragment extends BaseFragment {

    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = x.view().inject(this, inflater, container);
        view.setOnTouchListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

    }

    /**
     *  TODO 事件处理
     * @param
     * @return void
     * @Exception:
     */
    @Event(value = {})
    private void getEvent(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onRefreshComplete() {

    }
}
