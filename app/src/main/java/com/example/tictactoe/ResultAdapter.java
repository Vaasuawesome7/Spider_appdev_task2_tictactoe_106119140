package com.example.tictactoe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private String[] mNames;
    private int[] mScore;

    public ResultAdapter(String[] names, int[] score) {
        this.mNames = names;
        this.mScore = score;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.table_view, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        String show_name = mNames[position];
        int show_score = mScore[position];
        holder.text_name.setText(show_name);
        holder.text_number.setText(String.valueOf(show_score));
    }

    @Override
    public int getItemCount() {
        return getNonZeros();
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder{
        TextView text_name, text_number;
        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            text_name = itemView.findViewById(R.id.name);
            text_number = itemView.findViewById(R.id.number);
        }
    }

    private int getNonZeros() {
        int s = 0;
        for (int i = 0; i < 200; i++) {
            if (mScore[i] != 0)
                s++;
        }
        return s;
    }
}
