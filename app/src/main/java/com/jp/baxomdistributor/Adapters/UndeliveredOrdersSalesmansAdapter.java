package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.Interfaces.SalesmanSelectionListener;
import com.jp.baxomdistributor.databinding.EntitySalesmanNameUndeliveredOrdersBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 28-09-2021
 */
public class UndeliveredOrdersSalesmansAdapter extends RecyclerView.Adapter<UndeliveredOrdersSalesmansAdapter.MyHolder> {

    Context context;
    ArrayList<String> arrayList;

    SalesmanSelectionListener selectionListener;

    public UndeliveredOrdersSalesmansAdapter(Context context, ArrayList<String> arrayList, SalesmanSelectionListener selectionListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.selectionListener = selectionListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntitySalesmanNameUndeliveredOrdersBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.binding.tvSalesman.setTextColor(Color.WHITE);
        holder.binding.tvSalesman.setText("" + arrayList.get(position));

        holder.binding.tvSalesman.setOnClickListener(view -> selectionListener.onClick(arrayList.get(position)));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        EntitySalesmanNameUndeliveredOrdersBinding binding;

        public MyHolder(EntitySalesmanNameUndeliveredOrdersBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

}
