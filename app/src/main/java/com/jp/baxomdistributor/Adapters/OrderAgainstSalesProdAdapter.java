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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jp.baxomdistributor.Interfaces.OrderAgainstSalesProdQtyListener;
import com.jp.baxomdistributor.Interfaces.OrderAgainstSalesQtyListener;
import com.jp.baxomdistributor.Models.OrderAgainstSalesProdModel;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.databinding.EntityOrderAgainstSalesProdBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 13-04-2022
 */
public class OrderAgainstSalesProdAdapter extends RecyclerView.Adapter<OrderAgainstSalesProdAdapter.MyHolder> {

    String TAG = getClass().getSimpleName();
    ArrayList<OrderAgainstSalesProdModel> arrayList;
    ArrayList<Double> orderAgainstSchemeProdQtyModels;
    Context context;
    double start = 0, start_scheme = 0, start_replace = 0, start_shortage = 0;
    double product_price = 0, tot_order_price = 0, scheme_qty = 0, prod_qty = 0;

    OrderAgainstSalesProdQtyListener orderAgainstSalesProdQtyListener;
    OrderAgainstSalesQtyListener orderAgainstSalesQtyListener;

    int min_pos = 0;

    String is_add_scheme_qty_oas, is_add_replace_qty_oas, is_add_shortage_qty_oas;


    public OrderAgainstSalesProdAdapter(ArrayList<OrderAgainstSalesProdModel> arrayList, ArrayList<Double> orderAgainstSchemeProdQtyModels, Context context, OrderAgainstSalesProdQtyListener orderAgainstSalesProdQtyListener, OrderAgainstSalesQtyListener orderAgainstSalesQtyListener, String is_add_scheme_qty_oas, String is_add_replace_qty_oas, String is_add_shortage_qty_oas) {
        this.arrayList = arrayList;
        this.orderAgainstSchemeProdQtyModels = orderAgainstSchemeProdQtyModels;
        this.context = context;
        this.orderAgainstSalesProdQtyListener = orderAgainstSalesProdQtyListener;
        this.orderAgainstSalesQtyListener = orderAgainstSalesQtyListener;
        this.is_add_scheme_qty_oas = is_add_scheme_qty_oas;
        this.is_add_replace_qty_oas = is_add_replace_qty_oas;
        this.is_add_shortage_qty_oas = is_add_shortage_qty_oas;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntityOrderAgainstSalesProdBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {

        if (is_add_scheme_qty_oas.equalsIgnoreCase("0")) {
            holder.binding.imgPlusSchemeQty.setEnabled(false);
            holder.binding.imgMinusSchemeQty.setEnabled(false);
            holder.binding.edtSchemeQty.setEnabled(false);
        }

        if (is_add_replace_qty_oas.equalsIgnoreCase("0")) {
            holder.binding.imgPlusReplaceQty.setEnabled(false);
            holder.binding.imgMinusReplaceQty.setEnabled(false);
            holder.binding.edtReplacementQty.setEnabled(false);
        }

        if (is_add_shortage_qty_oas.equalsIgnoreCase("0")) {
            holder.binding.imgPlusShortageQty.setEnabled(false);
            holder.binding.imgMinusShortageQty.setEnabled(false);
            holder.binding.edtShortageQty.setEnabled(false);
        }


        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loading)
                .error(R.drawable.notfoundimages);

        Glide.with(context).load(arrayList.get(position).getProd_photo())
                .apply(options)
                .into(holder.binding.imgProd);

        holder.binding.tvProdName.setText("" + arrayList.get(position).getProd_name());
        holder.binding.tvProdUnit.setText("" + arrayList.get(position).getProd_unit());
        holder.binding.tvPtrRs.setText("" + arrayList.get(position).getPurchase_rate());
        holder.binding.tvBizRs.setText("" + arrayList.get(position).getSales_rate());
        holder.binding.tvStock.setText("" + arrayList.get(position).getStock_qty());
        holder.binding.tvDaySale.setText("" + arrayList.get(position).getOne_day_avg());

        scheme_qty = orderAgainstSchemeProdQtyModels.get(position);

        holder.binding.edtSchemeQty.setText("" + scheme_qty);

        if (!arrayList.get(position).getOrder_qty().equalsIgnoreCase("0")) {

            /*Log.i(TAG, "Order_qty............>" + arrayList.get(position).getOrder_qty());
            Log.i(TAG, "Purchase_rate............>" + arrayList.get(position).getPurchase_rate());*/

            holder.binding.edtProdDelQty.setText("" + arrayList.get(position).getOrder_qty());

            tot_order_price = Double.parseDouble(arrayList.get(position).getOrder_qty())
                    * Double.parseDouble(arrayList.get(position).getPurchase_rate());
            holder.binding.tvProdOrderRs.setText("" + String.format("%.2f", tot_order_price));

            orderAgainstSalesProdQtyListener.qtyChange(position,
                    Double.parseDouble(arrayList.get(position).getOrder_qty()));

        }
        getPointValue(holder, position, Double.parseDouble(arrayList.get(position).getPurchase_rate()));

        holder.binding.tvSchemeTitle.setOnClickListener(v -> getPointValue(holder, position, Double.parseDouble(arrayList.get(position).getPurchase_rate())));

        holder.binding.edtProdDelQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                product_price = Double.parseDouble(arrayList.get(position).getPurchase_rate());

                double del_qty;

                if (holder.binding.edtProdDelQty.getText().toString().isEmpty()) {
                    del_qty = 0.0;
                } else {
                    del_qty = Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim());
                }

                if (holder.binding.edtProdDelQty.getText().toString().isEmpty()) {

                    tot_order_price = 0 * product_price;
                    holder.binding.tvProdOrderRs.setText("" + String.format("%.2f", tot_order_price));

                    orderAgainstSalesProdQtyListener.qtyChange(position, 0);

                } else {

                    tot_order_price = Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim())
                            * product_price;
                    holder.binding.tvProdOrderRs.setText("" + String.format("%.2f", tot_order_price));

                    orderAgainstSalesProdQtyListener.qtyChange(position,
                            Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (holder.binding.edtProdDelQty.getText().toString().isEmpty())
                    holder.binding.edtProdDelQty.setText("0");

                getPointValue(holder, position, Double.parseDouble(arrayList.get(position).getPurchase_rate()));

            }
        });

        holder.binding.imgPlusOrderQty.setOnClickListener(view -> {

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

            product_price = Double.parseDouble(arrayList.get(position).getPurchase_rate());

            tot_order_price = qty * product_price;
            holder.binding.tvProdOrderRs.setText("" + String.format("%.2f", tot_order_price));

            getPointValue(holder, position, Double.parseDouble(arrayList.get(position).getPurchase_rate()));

            orderAgainstSalesProdQtyListener.qtyChange(position, qty);

        });

        holder.binding.imgMinusOrderQty.setOnClickListener(view -> {

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

                    holder.binding.edtProdDelQty.setText(arrayList.get(position).getArrayList_minvalue().get(min_pos) + "");
                    qty = Double.parseDouble(arrayList.get(position).getArrayList_minvalue().get(min_pos).toString());

                } else {

                    holder.binding.edtProdDelQty.setText("" + start);
                    qty = start;
                }

            } else if (Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) >
                    0 && Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) < 1) {

                min_pos--;
                holder.binding.edtProdDelQty.setText(arrayList.get(position).getArrayList_minvalue().get(min_pos) + "");
                qty = Double.parseDouble(arrayList.get(position).getArrayList_minvalue().get(min_pos).toString());
            } else {

                if (Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) > 0) {
                    holder.binding.edtProdDelQty.setText("" + (int) start);
                    qty = (int) start;
                }
            }

            product_price = Double.parseDouble(arrayList.get(position).getPurchase_rate());

            tot_order_price = qty * product_price;
            holder.binding.tvProdOrderRs.setText("" + String.format("%.2f", tot_order_price));

            getPointValue(holder, position, Double.parseDouble(arrayList.get(position).getPurchase_rate()));

            orderAgainstSalesProdQtyListener.qtyChange(position, qty);

        });

        holder.binding.edtSchemeQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (holder.binding.edtSchemeQty.getText().toString().isEmpty()) {

                    orderAgainstSalesQtyListener.qtyChange(position,
                            0,
                            Double.parseDouble(holder.binding.edtReplacementQty.getText().toString()),
                            Double.parseDouble(holder.binding.edtShortageQty.getText().toString()),
                            Double.parseDouble(holder.binding.tvRoundQty.getText().toString()));

                } else {

                    orderAgainstSalesQtyListener.qtyChange(position,
                            Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()),
                            Double.parseDouble(holder.binding.edtReplacementQty.getText().toString()),
                            Double.parseDouble(holder.binding.edtShortageQty.getText().toString()),
                            Double.parseDouble(holder.binding.tvRoundQty.getText().toString()));
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (holder.binding.edtSchemeQty.getText().toString().isEmpty())
                    holder.binding.edtSchemeQty.setText("0");

                getPointValue(holder, position, Double.parseDouble(arrayList.get(position).getPurchase_rate()));

            }
        });

        holder.binding.imgPlusSchemeQty.setOnClickListener(view -> {

            if (holder.binding.edtSchemeQty.getText().toString().trim().isEmpty()) {
                start_scheme = 0;
            } else {
                start_scheme = Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim());
            }

            start_scheme++;

            holder.binding.edtSchemeQty.setText("" + start_scheme);

            //holder.binding.edtSchemeQty.setText("" + start_scheme);

        });

        holder.binding.imgMinusSchemeQty.setOnClickListener(view -> {

            if (holder.binding.edtSchemeQty.getText().toString().trim().isEmpty()) {
                start_scheme = 0;
            } else {
                start_scheme = Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim());
            }
            if (start_scheme > 0) {

                start_scheme--;
                holder.binding.edtSchemeQty.setText("" + start_scheme);
            }
        });

        holder.binding.edtReplacementQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (holder.binding.edtReplacementQty.getText().toString().isEmpty()) {

                    orderAgainstSalesQtyListener.qtyChange(position,
                            Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()),
                            0,
                            Double.parseDouble(holder.binding.edtShortageQty.getText().toString()),
                            Double.parseDouble(holder.binding.tvRoundQty.getText().toString()));

                } else {

                    orderAgainstSalesQtyListener.qtyChange(position,
                            Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()),
                            Double.parseDouble(holder.binding.edtReplacementQty.getText().toString()),
                            Double.parseDouble(holder.binding.edtShortageQty.getText().toString()),
                            Double.parseDouble(holder.binding.tvRoundQty.getText().toString()));
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (holder.binding.edtReplacementQty.getText().toString().isEmpty())
                    holder.binding.edtReplacementQty.setText("0");

                getPointValue(holder, position, Double.parseDouble(arrayList.get(position).getPurchase_rate()));

            }
        });

        holder.binding.imgPlusReplaceQty.setOnClickListener(view -> {

            if (holder.binding.edtReplacementQty.getText().toString().trim().isEmpty()) {
                start_replace = 0;
            } else {
                start_replace = Double.parseDouble(holder.binding.edtReplacementQty.getText().toString().trim());
            }

            start_replace++;

            holder.binding.edtReplacementQty.setText("" + start_replace);
        });

        holder.binding.imgMinusReplaceQty.setOnClickListener(view -> {

            if (holder.binding.edtReplacementQty.getText().toString().trim().isEmpty()) {
                start_replace = 0;
            } else {
                start_replace = Double.parseDouble(holder.binding.edtReplacementQty.getText().toString().trim());
            }
            if (start_replace > 0) {

                start_replace--;
                holder.binding.edtReplacementQty.setText("" + start_replace);

            }
        });

        holder.binding.edtShortageQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (holder.binding.edtShortageQty.getText().toString().isEmpty()) {

                    orderAgainstSalesQtyListener.qtyChange(position,
                            Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()),
                            Double.parseDouble(holder.binding.edtReplacementQty.getText().toString()),
                            0,
                            Double.parseDouble(holder.binding.tvRoundQty.getText().toString()));

                } else {

                    orderAgainstSalesQtyListener.qtyChange(position,
                            Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()),
                            Double.parseDouble(holder.binding.edtReplacementQty.getText().toString()),
                            Double.parseDouble(holder.binding.edtShortageQty.getText().toString()),
                            Double.parseDouble(holder.binding.tvRoundQty.getText().toString()));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (holder.binding.edtShortageQty.getText().toString().isEmpty())
                    holder.binding.edtShortageQty.setText("0");

                getPointValue(holder, position, Double.parseDouble(arrayList.get(position).getPurchase_rate()));

            }
        });

        holder.binding.imgPlusShortageQty.setOnClickListener(view -> {

            if (holder.binding.edtShortageQty.getText().toString().trim().isEmpty()) {
                start_shortage = 0;
            } else {
                start_shortage = Double.parseDouble(holder.binding.edtShortageQty.getText().toString().trim());
            }

            start_shortage++;

            holder.binding.edtShortageQty.setText("" + start_shortage);

        });

        holder.binding.imgMinusShortageQty.setOnClickListener(view -> {

            if (holder.binding.edtShortageQty.getText().toString().trim().isEmpty()) {
                start_shortage = 0;
            } else {
                start_shortage = Double.parseDouble(holder.binding.edtShortageQty.getText().toString().trim());
            }
            if (start_shortage > 0) {

                start_shortage--;
                holder.binding.edtShortageQty.setText("" + start_replace);

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
        return arrayList.size();
    }


    public static class MyHolder extends RecyclerView.ViewHolder {

        EntityOrderAgainstSalesProdBinding binding;

        public MyHolder(EntityOrderAgainstSalesProdBinding b) {
            super(b.getRoot());
            binding = b;

        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void getPointValue(MyHolder holder, int pos, double pts_rs) {

        double total_qty = Double.parseDouble(holder.binding.edtProdDelQty.getText().toString())
                + Double.parseDouble(holder.binding.edtSchemeQty.getText().toString())
                + Double.parseDouble(holder.binding.edtReplacementQty.getText().toString())
                + Double.parseDouble(holder.binding.edtShortageQty.getText().toString());

//        Log.i(TAG, ".............total_qty...........>" + total_qty);

        String[] qty = Double.toString(total_qty).split("\\.");

        if (qty.length == 2) {
            double round_qty = 0;
            Log.i(TAG, "inside PointValue if edtProdDelQty_________>" + holder.binding.edtProdDelQty.getText().toString());
            Log.i(TAG, "inside PointValue if total_qty[0]_________>" + qty[0]);
            Log.i(TAG, "inside PointValue if total_qty[1]_________>" + qty[1]);

            //if (Double.parseDouble(qty[1]) > 0) {
            if (Double.parseDouble("." + qty[1]) >= .5) {

                Log.i(TAG, "inside PointValue if rount_qty______>" + String.format("%.2f", (1.0 - Double.parseDouble("." + qty[1]))));

                /*holder.binding.edtProdDelQty.setText("" +
                        (Double.parseDouble(holder.binding.edtProdDelQty.getText().toString())
                                + Double.parseDouble("." + qty[1])));*/
                round_qty = (1.0 - Double.parseDouble("." + qty[1]));
                holder.binding.tvRoundQty.setText("" + String.format("%.2f", round_qty));
            } else {

                Log.i(TAG, "inside PointValue else rount_qty______>" + "-." + qty[1]);

                /*holder.binding.edtProdDelQty.setText("" +
                        (Double.parseDouble(holder.binding.edtProdDelQty.getText().toString())
                                - Double.parseDouble("." + qty[1])));*/
                //holder.binding.tvRoundQty.setText("-." + qty[1]);

                round_qty = Double.parseDouble("." + qty[1]);
                holder.binding.tvRoundQty.setText("" + String.format("%.2f", round_qty));
            }
            //}


            if (holder.binding.tvRoundQty.getText().toString().equalsIgnoreCase("-.0")
                    || holder.binding.tvRoundQty.getText().toString().equalsIgnoreCase("-0.00")) {

                holder.binding.tvRoundQty.setText("0");
                orderAgainstSalesQtyListener.qtyChange(pos,
                        Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()),
                        Double.parseDouble(holder.binding.edtReplacementQty.getText().toString()),
                        Double.parseDouble(holder.binding.edtShortageQty.getText().toString()),
                        0);
            } else {
                orderAgainstSalesQtyListener.qtyChange(pos,
                        Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()),
                        Double.parseDouble(holder.binding.edtReplacementQty.getText().toString()),
                        Double.parseDouble(holder.binding.edtShortageQty.getText().toString()),
                        Double.parseDouble(holder.binding.tvRoundQty.getText().toString()));
            }

            orderAgainstSalesProdQtyListener.qtyChange(pos,
                    (Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim())
                            + round_qty));

//            Log.i(TAG, "inside PointValue edtProdDelQty.....................>" + holder.binding.edtProdDelQty.getText().toString().trim());
//            Log.i(TAG, "inside PointValue tot_order_price...................>" + tot_order_price);
//            Log.i(TAG, "inside PointValue tot_tvRoundQty...................>" + (Double.parseDouble(holder.binding.tvRoundQty.getText().toString().trim()) * pts_rs));

            holder.binding.tvProdOrderRs.setText("" + String.format("%.2f", ((Double.parseDouble(holder.binding.edtProdDelQty.getText().toString().trim()) * pts_rs)
                    + (round_qty * pts_rs))));
        }

    }
}
