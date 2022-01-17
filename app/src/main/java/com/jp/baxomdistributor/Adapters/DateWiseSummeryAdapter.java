package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.Interfaces.DelFailSelectionListener;
import com.jp.baxomdistributor.Models.DateWiseSummeryModel;
import com.jp.baxomdistributor.Models.DelFailReasonModel;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.databinding.EntityBussSummeryDateWiseBinding;
import com.jp.baxomdistributor.databinding.EntityDelFailReasonsBinding;
import com.jp.baxomdistributor.databinding.EntityDelFailShopCloseBinding;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 30-12-2021
 */
public class DateWiseSummeryAdapter extends RecyclerView.Adapter<DateWiseSummeryAdapter.MyHolder> {

    ArrayList<DateWiseSummeryModel> arrayList;
    Context context;

    public DateWiseSummeryAdapter(ArrayList<DateWiseSummeryModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntityBussSummeryDateWiseBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        if (position % 2 == 0)
            holder.binding.llDateWise.setBackgroundColor(context.getResources().getColor(R.color.white));
        else
            holder.binding.llDateWise.setBackgroundColor(context.getResources().getColor(R.color.light_pink));


        holder.binding.tvDateWiseDate.setText("" + arrayList.get(position).getDate());
        holder.binding.tvDateWisePtsValue.setText("" + arrayList.get(position).getPts_value());
        holder.binding.tvDateWisePtrValue.setText("" + arrayList.get(position).getPtr_value());
        holder.binding.tvDateWiseBizValue.setText("" + arrayList.get(position).getBiz_value());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        EntityBussSummeryDateWiseBinding binding;

        public MyHolder(EntityBussSummeryDateWiseBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

}
