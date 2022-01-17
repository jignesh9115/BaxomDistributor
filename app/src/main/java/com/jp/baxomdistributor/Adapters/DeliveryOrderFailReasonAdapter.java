package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jp.baxomdistributor.Interfaces.DelFailSelectionListener;
import com.jp.baxomdistributor.Interfaces.DeliveryProdQtyListener;
import com.jp.baxomdistributor.Models.DelFailReasonModel;
import com.jp.baxomdistributor.Models.DeliveryOrderProdModel;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.databinding.EntityDelFailReasonsBinding;
import com.jp.baxomdistributor.databinding.EntityDelFailShopCloseBinding;
import com.jp.baxomdistributor.databinding.EntityDeliveryOrderProdDetailBinding;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 30-12-2021
 */
public class DeliveryOrderFailReasonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<DelFailReasonModel> arrayList;
    Context context;
    DelFailSelectionListener delFailSelectionListener;

    public static final int shop_close = 1, reasons = 2;

    public DeliveryOrderFailReasonAdapter(ArrayList<DelFailReasonModel> arrayList, Context context, DelFailSelectionListener delFailSelectionListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.delFailSelectionListener = delFailSelectionListener;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return shop_close;
        else
            return reasons;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case shop_close:
                return new MyHolder1(EntityDelFailShopCloseBinding.inflate(LayoutInflater.from(context), parent, false));
            case reasons:
                return new MyHolder(EntityDelFailReasonsBinding.inflate(LayoutInflater.from(context), parent, false));

            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (position == 0) {

            MyHolder1 shop_close_holder = (MyHolder1) holder;

            if (arrayList.get(position).isIsselect()) {
                shop_close_holder.binding_shop_close.llSelection.setBackground(context.getResources().getDrawable(R.drawable.bg_del_fail_selected));
                shop_close_holder.binding_shop_close.tvReason.setTextColor(Color.WHITE);
            } else {
                shop_close_holder.binding_shop_close.llSelection.setBackground(context.getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                shop_close_holder.binding_shop_close.tvReason.setTextColor(Color.BLACK);
            }

            if (arrayList.get(position).getUri() != null) {
                shop_close_holder.binding_shop_close.tvReason.setTextSize(10);
                shop_close_holder.binding_shop_close.llPhotoOfShopclose.setVisibility(View.VISIBLE);
                shop_close_holder.binding_shop_close.imgPhotoofCloseshop.setImageURI(arrayList.get(position).getUri());
                shop_close_holder.binding_shop_close.tvReason.setText("Shop Close");

            } else {
                shop_close_holder.binding_shop_close.tvReason.setTextSize(14);
                shop_close_holder.binding_shop_close.tvReason.setText("Shop Close Photo");
                shop_close_holder.binding_shop_close.llPhotoOfShopclose.setVisibility(View.GONE);
                shop_close_holder.binding_shop_close.imgPhotoofCloseshop.setImageURI(arrayList.get(position).getUri());
            }
            PushDownAnim.setPushDownAnimTo(shop_close_holder.binding_shop_close.cardSelection)
                    .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                    .setOnClickListener(v -> {
                        arrayList.get(position).setIsselect(true);
                        setchecked(position);
                        delFailSelectionListener.delFailselection(position);
                    });
        } else {

            MyHolder reason_holder = (MyHolder) holder;
            reason_holder.binding.tvReason.setTextSize(14);
            reason_holder.binding.tvReason.setText("" + arrayList.get(position).getReason());

            if (arrayList.get(position).isIsselect()) {
                reason_holder.binding.llSelection.setBackground(context.getResources().getDrawable(R.drawable.bg_del_fail_selected));
                reason_holder.binding.tvReason.setTextColor(Color.WHITE);
            } else {
                reason_holder.binding.llSelection.setBackground(context.getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                reason_holder.binding.tvReason.setTextColor(Color.BLACK);
            }


            PushDownAnim.setPushDownAnimTo(reason_holder.binding.cardSelection)
                    .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                    .setOnClickListener(v -> {
                        arrayList.get(position).setIsselect(true);
                        setchecked(position);
                        delFailSelectionListener.delFailselection(position);
                    });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        EntityDelFailReasonsBinding binding;

        public MyHolder(EntityDelFailReasonsBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    public static class MyHolder1 extends RecyclerView.ViewHolder {

        EntityDelFailShopCloseBinding binding_shop_close;

        public MyHolder1(EntityDelFailShopCloseBinding b) {
            super(b.getRoot());
            binding_shop_close = b;
        }
    }

    public void setchecked(int pos) {

        for (int i = 0; i < arrayList.size(); i++) {
            if (pos != i) {
                arrayList.get(i).setIsselect(false);
            }
        }
    }

}
