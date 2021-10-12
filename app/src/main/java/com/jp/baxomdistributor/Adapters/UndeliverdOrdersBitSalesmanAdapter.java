package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.Interfaces.BitSalesmanClickListener;
import com.jp.baxomdistributor.Interfaces.SalesmanDateCheckedListener;
import com.jp.baxomdistributor.Models.UndeliveredOrdersSalesmanModel;
import com.jp.baxomdistributor.databinding.EntityBitSalesmansUndeliveredBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 28-09-2021
 */
public class UndeliverdOrdersBitSalesmanAdapter extends RecyclerView.Adapter<UndeliverdOrdersBitSalesmanAdapter.MyHolder> {

    ArrayList<UndeliveredOrdersSalesmanModel> arrayList;
    Context context;

    BitSalesmanClickListener bitSalesmanClickListener;

    SalesmanDateCheckedListener salesmanDateCheckedListener;

    public UndeliverdOrdersBitSalesmanAdapter(ArrayList<UndeliveredOrdersSalesmanModel> arrayList, Context context, BitSalesmanClickListener bitSalesmanClickListener, SalesmanDateCheckedListener salesmanDateCheckedListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.bitSalesmanClickListener = bitSalesmanClickListener;
        this.salesmanDateCheckedListener = salesmanDateCheckedListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntityBitSalesmansUndeliveredBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        holder.binding.tvDateSalesman.setText("" + arrayList.get(position).getEntry_date());
        holder.binding.tvSalesExeName.setText("" + arrayList.get(position).getSalesman());
        holder.binding.tvTotOrders.setText("" + arrayList.get(position).getTot_order());
        holder.binding.tvTotOrdersRs.setText("" + arrayList.get(position).getTot_amount());

        if (arrayList.get(position).getIs_pdf_generated().equalsIgnoreCase("true"))
            holder.binding.imgPdf.setVisibility(View.VISIBLE);
        else
            holder.binding.imgPdf.setVisibility(View.INVISIBLE);


        holder.binding.llSalesman.setOnClickListener(v -> {
            bitSalesmanClickListener.onclick(arrayList.get(position));
        });

        holder.binding.chkSales.setChecked(arrayList.get(position).isChecked());

        holder.binding.chkSales.setOnCheckedChangeListener((v, isChecked) -> {
            salesmanDateCheckedListener.onclick(arrayList.get(position), isChecked);
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class MyHolder extends RecyclerView.ViewHolder {

        EntityBitSalesmansUndeliveredBinding binding;

        public MyHolder(EntityBitSalesmansUndeliveredBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }


}
