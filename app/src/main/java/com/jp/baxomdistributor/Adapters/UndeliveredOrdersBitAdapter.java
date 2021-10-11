package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.Interfaces.BitClickListener;
import com.jp.baxomdistributor.Interfaces.BitSalesmanClickListener;
import com.jp.baxomdistributor.Interfaces.CheckBocCheckedListenter;
import com.jp.baxomdistributor.Interfaces.SalesmanDateCheckedListener;
import com.jp.baxomdistributor.Models.UdeliveredOrdersModel;
import com.jp.baxomdistributor.Models.UndeliveredOrdersSalesmanModel;
import com.jp.baxomdistributor.databinding.EntityBitListUndeliveredBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 28-09-2021
 */
public class UndeliveredOrdersBitAdapter extends RecyclerView.Adapter<UndeliveredOrdersBitAdapter.MyHolder> implements BitSalesmanClickListener,
        SalesmanDateCheckedListener {

    ArrayList<UdeliveredOrdersModel> arrayList_bit_orders;
    Context context;

    BitClickListener bitClickListener;

    SalesmanDateCheckedListener salesmanDateCheckedListener;

    public UndeliveredOrdersBitAdapter(ArrayList<UdeliveredOrdersModel> arrayList_bit_orders, Context context, BitClickListener bitClickListener, SalesmanDateCheckedListener salesmanDateCheckedListener) {
        this.arrayList_bit_orders = arrayList_bit_orders;
        this.context = context;
        this.bitClickListener = bitClickListener;
        this.salesmanDateCheckedListener = salesmanDateCheckedListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntityBitListUndeliveredBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        holder.binding.bitName.setText("" + arrayList_bit_orders.get(position).getBit_name());
        holder.binding.tvTotalOrders.setText("" + arrayList_bit_orders.get(position).getTot_order());
        holder.binding.tvTotOrdersRs.setText("₹ " + arrayList_bit_orders.get(position).getTot_amount() + " /-");

        UndeliverdOrdersBitSalesmanAdapter adapter = new UndeliverdOrdersBitSalesmanAdapter(
                arrayList_bit_orders.get(position).getArrayList(),
                context, this, this);

        holder.binding.rvSalesmanList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.binding.rvSalesmanList.setAdapter(adapter);

        holder.binding.chkMain.setOnCheckedChangeListener((compoundButton, b) -> {

            if (holder.binding.chkMain.isChecked()) {
                for (int i = 0; i < arrayList_bit_orders.get(position).getArrayList().size(); i++) {
                    arrayList_bit_orders.get(position).getArrayList().get(i).setChecked(true);
                }
            } else {
                for (int i = 0; i < arrayList_bit_orders.get(position).getArrayList().size(); i++) {
                    arrayList_bit_orders.get(position).getArrayList().get(i).setChecked(false);
                }
            }

            adapter.notifyDataSetChanged();

        });
    }

    @Override
    public int getItemCount() {
        return arrayList_bit_orders.size();
    }

    @Override
    public void onclick(UndeliveredOrdersSalesmanModel m) {
        bitClickListener.onclick(m);
    }

    @Override
    public void onclick(UndeliveredOrdersSalesmanModel model, boolean b) {
        salesmanDateCheckedListener.onclick(model, b);
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        EntityBitListUndeliveredBinding binding;

        public MyHolder(EntityBitListUndeliveredBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

}
