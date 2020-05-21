package com.example.payme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {

    private List<String> emailList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView email;

        public MyViewHolder(View view) {
            super(view);
            email = (TextView) view.findViewById(R.id.email_list);
        }
    }


    public FriendAdapter(List<String> emailList) {
        this.emailList = emailList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String friend = emailList.get(position);
        holder.email.setText(friend);

    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }
}
