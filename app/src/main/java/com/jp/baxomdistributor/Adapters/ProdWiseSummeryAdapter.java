package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.Models.DateWiseSummeryModel;
import com.jp.baxomdistributor.Models.ProdWiseSummeryModel;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.databinding.EntityBussSummeryDateWiseBinding;
import com.jp.baxomdistributor.databinding.EntityBussSummeryProdWiseBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 30-12-2021
 */
public class ProdWiseSummeryAdapter extends RecyclerView.Adapter<ProdWiseSummeryAdapter.MyHolder> {

    ArrayList<ProdWiseSummeryModel> arrayList;
    Context context;

    public ProdWiseSummeryAdapter(ArrayList<ProdWiseSummeryModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntityBussSummeryProdWiseBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        if (position % 2 == 0)
            holder.binding.llProduct.setBackgroundColor(context.getResources().getColor(R.color.white));
        else
            holder.binding.llProduct.setBackgroundColor(context.getResources().getColor(R.color.light_pink));

        holder.binding.tvProdWiseProd.setText("" + arrayList.get(position).getProd_name());
        holder.binding.tvProdWiseQty.setText("" + arrayList.get(position).getProd_qty());
        holder.binding.tvProdWisePts.setText("" + arrayList.get(position).getPts_value());
        holder.binding.tvProdWisePtr.setText("" + arrayList.get(position).getPtr_value());
        holder.binding.tvProdWiseBiz.setText("" + arrayList.get(position).getBiz_value());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        EntityBussSummeryProdWiseBinding binding;

        public MyHolder(EntityBussSummeryProdWiseBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

}
