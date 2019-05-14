package com.edu.sicnu.cs.zzy.mywork.Fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.edu.sicnu.cs.zzy.mywork.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_1 extends Fragment {
    private RecyclerView recyclerView;

    public Fragment_1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_1, container, false);


        return view;
    }
}
