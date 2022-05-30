package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.Models.DistributorStockModel;
import com.jp.baxomdistributor.databinding.EntityDistStockBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 13-04-2022
 */
public class DistributorStockAdapter extends RecyclerView.Adapter<DistributorStockAdapter.MyHolder> {

    ArrayList<DistributorStockModel> distributorStockModels;
    Context context;

    public DistributorStockAdapter(ArrayList<DistributorStockModel> distributorStockModels, Context context) {
        this.distributorStockModels = distributorStockModels;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntityDistStockBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.tvNo.setText("" + (position + 1));
        holder.binding.tvProdName.setText("" + distributorStockModels.get(position).getProd_name());
        holder.binding.tvCurrQty.setText("" + distributorStockModels.get(position).getProd_qty());
        holder.binding.edtCurrQty.setText("" + distributorStockModels.get(position).getProd_update_qty());
        holder.binding.tvAvgQty.setText("" + distributorStockModels.get(position).getAvg_21days());

        holder.binding.edtCurrQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                distributorStockModels.get(position).setProd_update_qty(s.toString());
                Log.i("DistributorStockAdapter", "inside s.toString...>" + s.toString());
                Log.i("DistributorStockAdapter", "inside getProd_update_qty...>" + distributorStockModels.get(position).getProd_update_qty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (holder.binding.edtCurrQty.getText().toString().isEmpty())
                    holder.binding.edtCurrQty.setText("0");

            }
        });


    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return distributorStockModels.size();
    }


    public static class MyHolder extends RecyclerView.ViewHolder {

        EntityDistStockBinding binding;

        public MyHolder(EntityDistStockBinding b) {
            super(b.getRoot());
            binding = b;

        }
    }

}
