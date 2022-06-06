package com.jp.baxomdistributor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jp.baxomdistributor.Interfaces.DeliverySchemeQtyListener;
import com.jp.baxomdistributor.Interfaces.OrderAgainstSalesSchemeQtyListener;
import com.jp.baxomdistributor.Models.MatchShemePOJO;
import com.jp.baxomdistributor.Models.ViewSchemesOrderPOJO;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.databinding.EntityOrderAgainstSalesSchemeBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 30-12-2021
 */
public class OrderAgainstSalesSchemeAdapter extends RecyclerView.Adapter<OrderAgainstSalesSchemeAdapter.MyHolder> {

    ArrayList<ViewSchemesOrderPOJO> arrayList;
    Context context;

    double start = 0;

    OrderAgainstSalesSchemeQtyListener orderAgainstSalesSchemeQtyListener;
    private final String TAG = getClass().getSimpleName();
    String is_add_scheme_detail_oas;

    public OrderAgainstSalesSchemeAdapter(ArrayList<ViewSchemesOrderPOJO> arrayList, Context context, OrderAgainstSalesSchemeQtyListener orderAgainstSalesSchemeQtyListener, String is_add_scheme_detail_oas) {
        this.arrayList = arrayList;
        this.context = context;
        this.orderAgainstSalesSchemeQtyListener = orderAgainstSalesSchemeQtyListener;
        this.is_add_scheme_detail_oas = is_add_scheme_detail_oas;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntityOrderAgainstSalesSchemeBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        if (is_add_scheme_detail_oas.equalsIgnoreCase("0")) {
            holder.binding.tvSchemePlusQty.setEnabled(false);
            holder.binding.tvSchemeMinusQty.setEnabled(false);
            holder.binding.edtSchemeDelQty.setEnabled(false);
        }

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loading)
                .error(R.drawable.notfoundimages);

        Glide.with(context).load(arrayList.get(position).getScheme_image())
                .apply(options)
                .into(holder.binding.imgSchemePhoto);

        Glide.with(context).load(arrayList.get(position).getResult_product_image())
                .apply(options)
                .into(holder.binding.imgResultProdPhoto);

        holder.binding.tvSchemeName.setText("" + arrayList.get(position).getScheme_name_long());
        holder.binding.tvSchemeOrderQty.setText("" + arrayList.get(position).getScheme_qty());
        holder.binding.tvConditionValue.setText("₹ " + arrayList.get(position).getScheme_price());
        holder.binding.tvResultProdValue.setText("₹ " + arrayList.get(position).getTotal_result_prod_value());

        holder.binding.edtSchemeDelQty.setText("" + arrayList.get(position).getScheme_stock_qty());

        arrayList.get(position).setScheme_qty(String.valueOf(
                Double.parseDouble(arrayList.get(position).getScheme_stock_qty())
                * Double.parseDouble(arrayList.get(position).getResult_product_qty())));

        orderAgainstSalesSchemeQtyListener.qtyChange(position,
                getTotalSchemeQty(arrayList.get(position).getResult_product_id()),
                arrayList.get(position).getResult_product_id());

        holder.binding.tvSchemePlusQty.setOnClickListener(v -> {

            double prev_qty = 0;
            if (holder.binding.edtSchemeDelQty.getText().toString().trim().isEmpty()) {
                start = 0;
            } else {
                start = Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString().trim());
                prev_qty = Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString().trim());
            }

            start++;


            holder.binding.edtSchemeDelQty.setText("" + start);

            /*holder.binding.tvSchemeValue.setText("" + String.format("%.2f",
                    (Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString())
                            * Double.parseDouble(arrayList.get(position).getScheme_price()))));*/

            /*Log.i(TAG, "prev_qty...>" + prev_qty);
            Log.i(TAG, "start...>" + start);
            Log.i(TAG, "(prev_qty - start)...>" + (start - prev_qty));*/

            // if (prev_qty == 0)

            Log.i(TAG, "Scheme_qty____>" + (start
                    * Double.parseDouble(arrayList.get(position).getResult_product_qty())));

            arrayList.get(position).setScheme_qty(String.valueOf(start
                    * Double.parseDouble(arrayList.get(position).getResult_product_qty())));

            orderAgainstSalesSchemeQtyListener.qtyChange(position,
                    getTotalSchemeQty(arrayList.get(position).getResult_product_id()),
                    arrayList.get(position).getResult_product_id());


        });

        holder.binding.tvSchemeMinusQty.setOnClickListener(v -> {

            if (holder.binding.edtSchemeDelQty.getText().toString().trim().isEmpty()) {
                start = 0;
            } else {
                start = Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString().trim());
            }

            if (start > 0) {

                if (arrayList.get(position).getIs_scheme_half().equalsIgnoreCase("0.5")) {

                    if (start == 1) {

                        holder.binding.edtSchemeDelQty.setText("0.5");

                    } else if (start == 0.5) {

                        holder.binding.edtSchemeDelQty.setText("0");

                    } else {

                        start--;
                        if (start >= 0) {
                            holder.binding.edtSchemeDelQty.setText("" + (int) start);
                        }

                    }

                } else if (arrayList.get(position).getIs_scheme_half().equalsIgnoreCase("0")) {

                    start--;

                    if (start > 0) {

                        holder.binding.edtSchemeDelQty.setText("" + (int) start);

                    } else {

                        holder.binding.edtSchemeDelQty.setText("0");

                    }

                }

                holder.binding.edtSchemeDelQty.setText("" + start);

                Log.i(TAG, "Scheme_qty____>" + (start
                        * Double.parseDouble(arrayList.get(position).getResult_product_qty())));

                arrayList.get(position).setScheme_qty(String.valueOf(start
                        * Double.parseDouble(arrayList.get(position).getResult_product_qty())));

                orderAgainstSalesSchemeQtyListener.qtyChange(position,
                        getTotalSchemeQty(arrayList.get(position).getResult_product_id()),
                        arrayList.get(position).getResult_product_id());

            }


        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        EntityOrderAgainstSalesSchemeBinding binding;

        public MyHolder(EntityOrderAgainstSalesSchemeBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    public double getTotalSchemeQty(String r_id) {

        double qty = 0;
        for (int i = 0; i < arrayList.size(); i++) {

            if (r_id.equalsIgnoreCase(arrayList.get(i).getResult_product_id())) {
                qty += Double.parseDouble(arrayList.get(i).getScheme_qty());
                Log.i(TAG, "getScheme_name_long_________>" + arrayList.get(i).getScheme_name_long());
                Log.i(TAG, "getResult_product_qty_______>" + arrayList.get(i).getResult_product_qty());
                Log.i(TAG, "getScheme_qty_______________>" + arrayList.get(i).getScheme_qty());
            }

        }

        return qty;
    }
}
