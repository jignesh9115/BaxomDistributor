package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.Interfaces.SalesmanSelectionListener;
import com.jp.baxomdistributor.Models.UdeliveredOrdersSalesmanSelectionModel;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.databinding.EntitySalesmanNameUndeliveredOrdersBinding;
import com.jp.baxomdistributor.databinding.EntitySalesmanSelectionUndeliveredOrdersBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 28-09-2021
 */
public class UndeliveredOrdersSalesmansAdapter extends RecyclerView.Adapter<UndeliveredOrdersSalesmansAdapter.MyHolder> {

    Context context;
    ArrayList<UdeliveredOrdersSalesmanSelectionModel> arrayList;

    SalesmanSelectionListener selectionListener;

    public UndeliveredOrdersSalesmansAdapter(Context context, ArrayList<UdeliveredOrdersSalesmanSelectionModel> arrayList, SalesmanSelectionListener selectionListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.selectionListener = selectionListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntitySalesmanSelectionUndeliveredOrdersBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        holder.binding.llSalesman.setBackground(context.getDrawable(R.drawable.salesman_bg_select));

        if (arrayList.get(position).getTicks().equalsIgnoreCase("0")) {
            holder.binding.tvTicks.setText("");
            holder.binding.llSalesman.setBackground(context.getDrawable(R.drawable.salesman_bg_unselect));
        }

        if (arrayList.get(position).getTicks().equalsIgnoreCase("1")) {
            holder.binding.tvTicks.setText("✓");
        }
        if (arrayList.get(position).getTicks().equalsIgnoreCase("2")) {
            holder.binding.tvTicks.setText("✓✓");
        }
        if (arrayList.get(position).getTicks().equalsIgnoreCase("3")) {
            holder.binding.tvTicks.setText("✓✓✓");
        }
        if (arrayList.get(position).getTicks().equalsIgnoreCase("4")) {
            holder.binding.tvTicks.setText("✓✓✓\n✓");
        }

        if (arrayList.get(position).getTicks().equalsIgnoreCase("5")) {
            holder.binding.tvTicks.setVisibility(View.VISIBLE);
            holder.binding.tvTicks.setText("✓✓✓\n✓✓");
        }


        holder.binding.tvSalesman.setTextColor(Color.WHITE);
        holder.binding.tvSalesman.setText("" + arrayList.get(position).getSalesman_name());

        holder.binding.llSalesman.setOnClickListener(view -> selectionListener.onClick(arrayList.get(position).getSalesman_name()));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        EntitySalesmanSelectionUndeliveredOrdersBinding binding;

        public MyHolder(EntitySalesmanSelectionUndeliveredOrdersBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

}
