package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.databinding.EntitySalesmanNameUndeliveredOrdersBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 28-09-2021
 */
public class UndeliveredOrdersDatesAdapter extends RecyclerView.Adapter<UndeliveredOrdersDatesAdapter.MyHolder> {

    Context context;
    ArrayList<String> arrayList;

    private UndeliveredOrdersDatesAdapter.OnItemClickListener mListener;

    public UndeliveredOrdersDatesAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public void setOnItemClickListener(UndeliveredOrdersDatesAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntitySalesmanNameUndeliveredOrdersBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        holder.binding.tvSalesman.setText("" + arrayList.get(position));

        holder.binding.tvSalesman.setOnLongClickListener(view -> {
            mListener.onItemClick(arrayList.get(position));
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String salesman);
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        EntitySalesmanNameUndeliveredOrdersBinding binding;

        public MyHolder(EntitySalesmanNameUndeliveredOrdersBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

}
