package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.Models.SchemeWiseBussSummeryModel;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.databinding.EntityBussSummerySchemeSummeryBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 30-12-2021
 */
public class SchemeWiseBussSummeryAdapter extends RecyclerView.Adapter<SchemeWiseBussSummeryAdapter.MyHolder> {

    ArrayList<SchemeWiseBussSummeryModel> arrayList;
    Context context;

    public SchemeWiseBussSummeryAdapter(ArrayList<SchemeWiseBussSummeryModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntityBussSummerySchemeSummeryBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        if (position % 2 == 0)
            holder.binding.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        else
            holder.binding.llMain.setBackgroundColor(context.getResources().getColor(R.color.light_pink));

        holder.binding.tvSchemeDetail.setText(arrayList.get(position).getScheme_name()
                + "\n(" + arrayList.get(position).getResult_prod_name() + ")");
        holder.binding.tvSchemeWiseQty.setText("" + arrayList.get(position).getResult_prod_qty());
        holder.binding.tvSchemeBiz.setText("" + arrayList.get(position).getScheme_biz());
        holder.binding.tvSchemeDiscPts.setText("" + arrayList.get(position).getDisc_pts());
        holder.binding.tvSchemeDiscBiz.setText("" + arrayList.get(position).getDisc_biz());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        EntityBussSummerySchemeSummeryBinding binding;

        public MyHolder(EntityBussSummerySchemeSummeryBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

}
