package com.edu.sicnu.cs.zzy.mywork.Fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edu.sicnu.cs.zzy.mywork.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_3 extends Fragment {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView textView;
    public Fragment_3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_3, container, false);
        textView = view.findViewById(R.id.usr_textinfo);
        sharedPreferences = getActivity().getSharedPreferences("login_status",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.getString("islogin","").equals("true")){
            textView.setText("当前用户: "+sharedPreferences.getString("usr",""));
        }else{
            textView.setText("您还没有登陆");
        }

        return view;
    }

}
