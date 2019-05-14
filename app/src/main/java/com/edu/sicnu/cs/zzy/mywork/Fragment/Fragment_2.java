package com.edu.sicnu.cs.zzy.mywork.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edu.sicnu.cs.zzy.mywork.Music;
import com.edu.sicnu.cs.zzy.mywork.PlayerActivity;
import com.edu.sicnu.cs.zzy.mywork.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_2 extends Fragment {
    private static final String TAG = "Fragment_2";
    private ArrayList<Music> musiclist = new ArrayList<>();
    private RecyclerView recyclerView;
    private Intent intent;
    public Fragment_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_2, container, false);
        Bundle bundle = this.getArguments();
        musiclist = (ArrayList<Music>) bundle.getSerializable("array");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = view.findViewById(R.id.Musiclist);
        recyclerView.setLayoutManager(linearLayoutManager);
        MusicAdapter adapter = new MusicAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,viewGroup,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            Music music = musiclist.get(i);
            viewHolder.textView_musicinfo.setText(music.getMusicName());
            viewHolder.textView_musicartist.setText(music.getMusicArtist());
            final int position = i;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra("number",position);
                    intent.putExtra("musiclist",musiclist);
                    startActivity(intent);
                }
            });



        }

        @Override
        public int getItemCount() {
            if(musiclist == null){return  0;}
            return musiclist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView textView_musicinfo,textView_musicartist;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView_musicinfo = itemView.findViewById(R.id.musicInfo);
                textView_musicartist = itemView.findViewById(R.id.musicArtist);
            }
        }
    }
}
