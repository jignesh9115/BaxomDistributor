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

import com.jp.baxomdistributor.Models.DistributorSchemeStockModel;
import com.jp.baxomdistributor.Models.DistributorStockModel;
import com.jp.baxomdistributor.databinding.EntityDistSchemeStockBinding;
import com.jp.baxomdistributor.databinding.EntityDistStockBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 13-04-2022
 */
public class DistributorSchemeStockAdapter extends RecyclerView.Adapter<DistributorSchemeStockAdapter.MyHolder> {

    ArrayList<DistributorSchemeStockModel> distributorStockModels;
    Context context;

    public DistributorSchemeStockAdapter(ArrayList<DistributorSchemeStockModel> distributorStockModels, Context context) {
        this.distributorStockModels = distributorStockModels;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntityDistSchemeStockBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.tvNo.setText("" + (position + 1));
        holder.binding.tvSchemeName.setText("" + distributorStockModels.get(position).getScheme_name());
        holder.binding.tvCurrQty.setText("" + distributorStockModels.get(position).getScheme_qty());
        holder.binding.edtCurrQty.setText("" + distributorStockModels.get(position).getScheme_qty());

        holder.binding.edtCurrQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                distributorStockModels.get(position).setScheme_update_qty(s.toString());
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

        EntityDistSchemeStockBinding binding;

        public MyHolder(EntityDistSchemeStockBinding b) {
            super(b.getRoot());
            binding = b;

        }
    }

}
