package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jp.baxomdistributor.Interfaces.DeliveryProdQtyListener;
import com.jp.baxomdistributor.Models.DeliveryOrderProdModel;
import com.jp.baxomdistributor.Models.UpdateSalesOrderProductPOJO;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.databinding.EntityDeliveryOrderProdDetailBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 30-12-2021
 */
public class DeliveryOrderProdAdapter extends RecyclerView.Adapter<DeliveryOrderProdAdapter.MyHolder> {

    ArrayList<DeliveryOrderProdModel> arrayList;
    Context context;

    DeliveryProdQtyListener deliveryProdQtyListener;

    double start = 0;
    double product_price = 0, tot_order_price = 0;

    int min_pos = 0;

    public DeliveryOrderProdAdapter(ArrayList<DeliveryOrderProdModel> arrayList, Context context, DeliveryProdQtyListener deliveryProdQtyListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.deliveryProdQtyListener = deliveryProdQtyListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntityDeliveryOrderProdDetailBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loading)
                .error(R.drawable.notfoundimages);

        Glide.with(context).load(arrayList.get(position).getProd_img())
                .apply(options)
                .into(holder.binding.imgProd);

        holder.binding.tvProdName.setText("" + arrayList.get(position).getProd_name());
        holder.binding.tvProdUnit.setText("" + arrayList.get(position).getProd_unit());

        holder.binding.tvPtrRs.setText("" + arrayList.get(position).getMrp_rs());
        holder.binding.tvTpRs.setText("" + arrayList.get(position).getProd_price());
        holder.binding.tvMrpRs.setText("" + arrayList.get(position).getTp_rs());

        holder.binding.tvOrderQuantity.setText("" + arrayList.get(position).getProd_qty());
        holder.binding.edtProdDelQty.setText("" + arrayList.get(position).getProd_del_qty());

        holder.binding.tvOrderRs.setText("" + (Double.parseDouble(arrayList.get(position).getProd_qty()) *
                Double.parseDouble(arrayList.get(position).getMrp_rs())));

        holder.binding.tvDeliveryRs.setText("" + (Double.parseDouble(arrayList.get(position).getProd_del_qty()) *
                Double.parseDouble(arrayList.get(position).getMrp_rs())));


        holder.binding.edtProdDelQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                product_price = Double.parseDouble(arrayList.get(position).getMrp_rs());

                double del_qty;

                if (holder.binding.edtProdDelQty.getText().toString().isEmpty()) {
                    del_qty = 0.0;
                } else {
                    del_qty = Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim());
                }

                if (holder.binding.edtProdDelQty.getText().toString().isEmpty()) {

                    tot_order_price = 0 * product_price;
                    holder.binding.tvDeliveryRs.setText("" + (int) tot_order_price);

                    deliveryProdQtyListener.qtyChange(position, 0);

                } else {

                    tot_order_price = Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) * product_price;
                    holder.binding.tvDeliveryRs.setText("" + (int) tot_order_price);

                    deliveryProdQtyListener.qtyChange(position, Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        holder.binding.imgProductPlusQty.setOnClickListener(view -> {

            double qty = 0.0;

            if (holder.binding.edtProdDelQty.getText().toString().trim().isEmpty()) {
                start = 0;
            } else {
                start = Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim());
            }

            start++;

            if ((int) Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) < 1)
                min_pos = 1 + arrayList.get(position).getArrayList_minvalue()
                        .indexOf(Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()));

            if (Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) < 1
                    && min_pos < arrayList.get(position).getArrayList_minvalue().size()) {

                holder.binding.edtProdDelQty.setText("" + arrayList.get(position).getArrayList_minvalue().get(min_pos));
                qty = arrayList.get(position).getArrayList_minvalue().get(min_pos);
                min_pos++;

            } else {

                holder.binding.edtProdDelQty.setText("" + (int) start);
                qty = (int) start;
            }

            product_price = Double.parseDouble(arrayList.get(position).getMrp_rs());

            tot_order_price = qty * product_price;
            holder.binding.tvDeliveryRs.setText("" + (int) tot_order_price);

            deliveryProdQtyListener.qtyChange(position, qty);
        });

        holder.binding.imgProductMinusQty.setOnClickListener(view -> {

            double qty = 0.0;

            if (holder.binding.edtProdDelQty.getText().toString().trim().isEmpty()) {
                start = 0;
            } else {
                start = Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim());
            }

            start--;
            if ((int) Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) < 1)
                min_pos = arrayList.get(position).getArrayList_minvalue().indexOf(Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()));

            if (Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) == 1) {

                if (arrayList.get(position).getArrayList_minvalue().size() > 1) {

                    min_pos = arrayList.get(position).getArrayList_minvalue().size() - 1;

                    holder.binding.edtProdDelQty.setText(arrayList.get(position).getArrayList_minvalue().get(min_pos).toString());
                    qty = Double.parseDouble(arrayList.get(position).getArrayList_minvalue().get(min_pos).toString());

                } else {

                    holder.binding.edtProdDelQty.setText("" + start);
                    qty = start;
                }

            } else if (Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) >
                    0 && Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) < 1) {

                min_pos--;
                holder.binding.edtProdDelQty.setText(arrayList.get(position).getArrayList_minvalue().get(min_pos).toString());
                qty = Double.parseDouble(arrayList.get(position).getArrayList_minvalue().get(min_pos).toString());
            } else {

                if (Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) > 0) {
                    holder.binding.edtProdDelQty.setText("" + (int) start);
                    qty = (int) start;
                }
            }

            product_price = Double.parseDouble(arrayList.get(position).getMrp_rs());

            tot_order_price = qty * product_price;
            holder.binding.tvDeliveryRs.setText("" + (int) tot_order_price);

            deliveryProdQtyListener.qtyChange(position, qty);
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        EntityDeliveryOrderProdDetailBinding binding;

        public MyHolder(EntityDeliveryOrderProdDetailBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

}
