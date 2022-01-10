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
import com.jp.baxomdistributor.Models.DeliveryOrderSchemeModel;
import com.jp.baxomdistributor.Models.MatchShemePOJO;
import com.jp.baxomdistributor.Models.SchemesOrderPOJO;
import com.jp.baxomdistributor.Models.ViewSchemesOrderPOJO;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.databinding.EntityDeliveryOrderSchemeDetailBinding;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 30-12-2021
 */
public class DeliveryOrderSchemeAdapter extends RecyclerView.Adapter<DeliveryOrderSchemeAdapter.MyHolder> {

    ArrayList<ViewSchemesOrderPOJO> arrayList;
    ArrayList<MatchShemePOJO> arrayList_match_scheme_list, arrayList_total_scheme_order_list;
    Context context;

    boolean isSchemeCheck = false;
    double tot_order_rs = 0.0, tot_deliver_rs = 0.0, tot_scheme_order_rs = 0.0, start = 0;

    DeliverySchemeQtyListener deliverySchemeQtyListener;
    private final String TAG = getClass().getSimpleName();

    public DeliveryOrderSchemeAdapter(ArrayList<ViewSchemesOrderPOJO> arrayList, ArrayList<MatchShemePOJO> arrayList_match_scheme_list, ArrayList<MatchShemePOJO> arrayList_total_scheme_order_list, Context context, boolean isSchemeCheck, double tot_order_rs, double tot_deliver_rs, double tot_scheme_order_rs, DeliverySchemeQtyListener deliverySchemeQtyListener) {
        this.arrayList = arrayList;
        this.arrayList_match_scheme_list = arrayList_match_scheme_list;
        this.arrayList_total_scheme_order_list = arrayList_total_scheme_order_list;
        this.context = context;
        this.isSchemeCheck = isSchemeCheck;
        this.tot_order_rs = tot_order_rs;
        this.tot_deliver_rs = tot_deliver_rs;
        this.tot_scheme_order_rs = tot_scheme_order_rs;
        this.deliverySchemeQtyListener = deliverySchemeQtyListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(EntityDeliveryOrderSchemeDetailBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        holder.binding.edtSchemeDelQty.setEnabled(false);
        holder.binding.tvSchemePlusQty.setEnabled(false);
        holder.binding.tvSchemeMinusQty.setEnabled(false);

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

        if (isSchemeCheck) {

            holder.binding.edtSchemeDelQty.setText("0");
            holder.binding.tvSchemeValue.setText("0.0");
            holder.binding.tvSchemeOrderValue.setText(" / 0.0");

            if (arrayList.get(position).getScheme_type_id().equalsIgnoreCase("1")) {

                if (tot_deliver_rs > (Double.parseDouble(arrayList.get(position).getScheme_price()) * 0.6)
                        && tot_deliver_rs < Double.parseDouble(arrayList.get(position).getScheme_price())) {

                    holder.binding.rlSchemeDetail.setBackground(context.getDrawable(R.drawable.scheme_bg_orange));

                    holder.binding.tvSchemePlusQty.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.tvSchemeMinusQty.setBackgroundColor(Color.parseColor("#FF9800"));

                    holder.binding.view1.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view2.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view3.setBackgroundColor(Color.parseColor("#FF9800"));

                    holder.binding.view1ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view2ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view3ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view4ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view5ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view6ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view7ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));


                } else if (tot_deliver_rs >= Double.parseDouble(arrayList.get(position).getScheme_price())) {

                    holder.binding.rlSchemeDetail.setBackground(context.getDrawable(R.drawable.scheme_bg_green));

                    holder.binding.tvSchemePlusQty.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.tvSchemeMinusQty.setBackgroundColor(Color.parseColor("#27ae60"));


                    holder.binding.view1.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view2.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view3.setBackgroundColor(Color.parseColor("#27ae60"));

                    holder.binding.view1ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view2ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view3ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view4ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view5ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view6ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view7ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));

                    holder.binding.tvSchemePlusQty.setEnabled(true);
                    holder.binding.tvSchemeMinusQty.setEnabled(true);
                }

                holder.binding.tvSchemeOrderValue.setText(" / " + tot_deliver_rs);

            } else {

                if (Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price())
                        > (Double.parseDouble(arrayList.get(position).getScheme_price()) * 0.6)
                        && Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price())
                        < Double.parseDouble(arrayList.get(position).getScheme_price())) {

                    holder.binding.rlSchemeDetail.setBackground(context.getDrawable(R.drawable.scheme_bg_orange));

                    holder.binding.tvSchemePlusQty.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.tvSchemeMinusQty.setBackgroundColor(Color.parseColor("#FF9800"));

                    holder.binding.view1.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view2.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view3.setBackgroundColor(Color.parseColor("#FF9800"));

                    holder.binding.view1ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view2ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view3ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view4ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view5ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view6ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                    holder.binding.view7ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));

                } else if (Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price())
                        >= Double.parseDouble(arrayList.get(position).getScheme_price())) {

                    holder.binding.rlSchemeDetail.setBackground(context.getDrawable(R.drawable.scheme_bg_green));

                    holder.binding.tvSchemePlusQty.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.tvSchemeMinusQty.setBackgroundColor(Color.parseColor("#27ae60"));

                    holder.binding.view1.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view2.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view3.setBackgroundColor(Color.parseColor("#27ae60"));

                    holder.binding.view1ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view2ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view3ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view4ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view5ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view6ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                    holder.binding.view7ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));

                    holder.binding.tvSchemePlusQty.setEnabled(true);
                    holder.binding.tvSchemeMinusQty.setEnabled(true);
                }

                holder.binding.tvSchemeOrderValue.setText(" / " + arrayList_match_scheme_list.get(position).getScheme_price());
            }

            if (arrayList.get(position).getIs_scheme_half().equalsIgnoreCase("0.5")) {

                if (Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) >
                        (Double.parseDouble(arrayList.get(position).getScheme_price()) * 0.5)) {

                    holder.binding.tvSchemePlusQty.setEnabled(true);
                    holder.binding.tvSchemeMinusQty.setEnabled(true);
                }
            }

        } else {

            if (Double.parseDouble(arrayList.get(position).getScheme_qty_del()) > 0) {

                holder.binding.edtSchemeDelQty.setText("" + arrayList.get(position).getScheme_qty_del());
                holder.binding.tvSchemeValue.setText("" + (Double.parseDouble(arrayList.get(position).getScheme_qty_del())
                        * Double.parseDouble(arrayList.get(position).getScheme_price())));
                holder.binding.tvSchemeOrderValue.setText(" / " + Double.parseDouble(arrayList.get(position).getScheme_price()));


            } else {

                holder.binding.edtSchemeDelQty.setText("" + arrayList.get(position).getScheme_qty());
                holder.binding.tvSchemeValue.setText("" + (Double.parseDouble(arrayList.get(position).getScheme_qty())
                        * Double.parseDouble(arrayList.get(position).getScheme_price())));
                holder.binding.tvSchemeOrderValue.setText(" / " + Double.parseDouble(arrayList.get(position).getScheme_price()));

            }
        }

        holder.binding.tvSchemePlusQty.setOnClickListener(v -> {

            if (holder.binding.edtSchemeDelQty.getText().toString().trim().isEmpty()) {
                start = 0;
            } else {
                start = Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString().trim());
            }

            Log.i(TAG, "tot_order_rs=>" + tot_order_rs);
            Log.i(TAG, "TotalOrderRs=>" + tot_deliver_rs);
            Log.i(TAG, "SchemeValue=>" + (start * Double.parseDouble(arrayList.get(position).getScheme_price())));
            Log.i(TAG, "SchemeOrderValue=>" + holder.binding.tvSchemeOrderValue.getText().toString().trim());


            if (tot_scheme_order_rs > tot_deliver_rs) {

                Toast.makeText(context, "scheme value greater than scheme order", Toast.LENGTH_SHORT).show();

                if (Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) >
                        (Double.parseDouble(arrayList.get(position).getScheme_price()) * 0.5) &&
                        Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) <
                                (Double.parseDouble(arrayList.get(position).getScheme_price()))) {

                    Toast.makeText(context, "scheme value greater than scheme order", Toast.LENGTH_SHORT).show();
                    //holder.binding.edtSchemeQty.setText("" + ((int) start - 0.5));

                } else {
                    Toast.makeText(context, "scheme value greater than scheme order", Toast.LENGTH_SHORT).show();
                    //holder.binding.edtSchemeQty.setText("" + ((int) start - 1));
                }
            } else {

                if (arrayList.get(position).getIs_scheme_half().equalsIgnoreCase("0.5")) {

                    if ((Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString())
                            * Double.parseDouble(arrayList.get(position).getScheme_price()) >
                            Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")))) {

                        Toast.makeText(context, "scheme value greater than scheme order", Toast.LENGTH_SHORT).show();

                    } else {

                        if (Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString().trim()) == 0) {

                            holder.binding.edtSchemeDelQty.setText("0.5");

                        } else if (Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString().trim()) >= 0.5) {

                            start++;
                            holder.binding.edtSchemeDelQty.setText("" + (int) start);
                        }
                    }

                } else if (arrayList.get(position).getIs_scheme_half().equalsIgnoreCase("0")) {

                    if (((Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString()) + 1)
                            * Double.parseDouble(arrayList.get(position).getScheme_price()) >
                            Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")))) {

                        Toast.makeText(context, "scheme value greater than scheme order", Toast.LENGTH_SHORT).show();

                    } else {

                        if (arrayList.get(position).getIs_scheme_half().equalsIgnoreCase("0")) {

                            if (start >= 0) {
                                start++;
                                holder.binding.edtSchemeDelQty.setText("" + (int) start);
                            }
                        }

                    }

                }

                if (((Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString()))
                        * Double.parseDouble(arrayList.get(position).getScheme_price()) >
                        Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")))) {

                    if (Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) >
                            (Double.parseDouble(arrayList.get(position).getScheme_price()) * 0.5) &&
                            Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) <
                                    (Double.parseDouble(arrayList.get(position).getScheme_price()))) {

                        //Log.i(TAG, "inside if starts=>" + start);

                        Toast.makeText(context, "scheme value greater than scheme order", Toast.LENGTH_SHORT).show();
                        holder.binding.edtSchemeDelQty.setText("" + ((int) start - 0.5));

                    } else {

                        //Log.i(TAG, "inside else starts=>" + start);

                        Toast.makeText(context, "scheme value greater than scheme order", Toast.LENGTH_SHORT).show();
                        holder.binding.edtSchemeDelQty.setText("" + ((int) start - 1));
                    }

                } else {

                    holder.binding.tvSchemeValue.setText("" +
                            String.format("%.2f", (Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString())
                                    * Double.parseDouble(arrayList.get(position).getScheme_price()))));
                }


                double cmp_scheme_value = (Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString()) *
                        Double.parseDouble(arrayList.get(position).getScheme_price()));

                double schme_value = Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().replace("/", ""));

                double tot_scheme_value = cmp_scheme_value + compareSchemes(arrayList.get(position).getScheme_id());


                   /* Log.i(TAG, "compareSchemes==>" + compareSchemes(arrayList.get(position).getScheme_id()));
                    Log.i(TAG, "tot_scheme_value==>" + tot_scheme_value);*/


                if (tot_scheme_value > schme_value) {

                    Toast.makeText(context, "...scheme value > scheme order...", Toast.LENGTH_SHORT).show();

                    if (Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) >
                            (Double.parseDouble(arrayList.get(position).getScheme_price()) * 0.5) &&
                            Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) <
                                    (Double.parseDouble(arrayList.get(position).getScheme_price()))) {

                        //Log.i(TAG, "inside if starts=>" + holder.binding.edtSchemeDelQty.getText().toString());

                        Toast.makeText(context, "scheme value greater than scheme order", Toast.LENGTH_SHORT).show();

                        if (holder.binding.edtSchemeDelQty.getText().toString().equalsIgnoreCase("0.5"))
                            holder.binding.edtSchemeDelQty.setText("0");
                        else if (holder.binding.edtSchemeDelQty.getText().toString().equalsIgnoreCase("0") ||
                                holder.binding.edtSchemeDelQty.getText().toString().equalsIgnoreCase("0.0"))
                            holder.binding.edtSchemeDelQty.setText("0");
                        else
                            holder.binding.edtSchemeDelQty.setText("" + ((int) start - 0.5));

                    } else {

                        //Log.i(TAG, "inside else starts=>" + holder.binding.edtSchemeDelQty.getText().toString());
                        Toast.makeText(context, "scheme value greater than scheme order", Toast.LENGTH_SHORT).show();

                        if (holder.binding.edtSchemeDelQty.getText().toString().equalsIgnoreCase("0.5"))
                            holder.binding.edtSchemeDelQty.setText("0");
                        else if (holder.binding.edtSchemeDelQty.getText().toString().equalsIgnoreCase("0") ||
                                holder.binding.edtSchemeDelQty.getText().toString().equalsIgnoreCase("0.0"))
                            holder.binding.edtSchemeDelQty.setText("0");
                        else
                            holder.binding.edtSchemeDelQty.setText("" + ((int) start - 1));
                    }

                    holder.binding.tvSchemeValue.setText("" + String.format("%.2f",
                            (Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString()) *
                                    Double.parseDouble(arrayList.get(position).getScheme_price()))));

                }


            }

            deliverySchemeQtyListener.schemeQtyScheme(position,
                    Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")),
                    Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString().trim()));

        });

        holder.binding.tvSchemeMinusQty.setOnClickListener(v -> {

            if (holder.binding.edtSchemeDelQty.getText().toString().trim().isEmpty()) {
                start = 0;
            } else {
                start = Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString().trim());
            }

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

            holder.binding.tvSchemeValue.setText("" + String.format("%.2f",
                    (Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString())
                            * Double.parseDouble(arrayList.get(position).getScheme_price()))));

            deliverySchemeQtyListener.schemeQtyScheme(position,
                    Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")),
                    Double.parseDouble(holder.binding.edtSchemeDelQty.getText().toString().trim()));


        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        EntityDeliveryOrderSchemeDetailBinding binding;

        public MyHolder(EntityDeliveryOrderSchemeDetailBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    public double compareSchemes(String cmp_scheme_id) {

        boolean isMatched = false;

        double tot_SchemeValue = 0.0;
        int pos = 0;

        for (int i = 0; i < arrayList.size(); i++) {
            if (cmp_scheme_id.equalsIgnoreCase(arrayList.get(i).getScheme_id())) {
                pos = i;
            }
        }

        for (int i = 0; i < arrayList.size(); i++) {

            if (!cmp_scheme_id.equalsIgnoreCase(arrayList.get(i).getScheme_id())) {

                if (arrayList.get(pos).getArrayList().size() == arrayList.get(i).getArrayList().size()) {

                    //Log.i(TAG, "====>inside if match no of prod");
                    //Log.i(TAG, "=====Cmp_no of prod==>" + arrayList.get(pos).getArrayList().size());
                    //Log.i(TAG, "=====no of prod==>" + arrayList.get(i).getArrayList().size());

                    for (int j = 0; j < arrayList.get(i).getArrayList().size(); j++) {

                        if (arrayList.get(pos).getArrayList().get(j).getProduct_id()
                                .equalsIgnoreCase(arrayList.get(pos).getArrayList().get(j).getProduct_id())) {
                            isMatched = true;

                            //Log.i(TAG, "=======>inside if match prodId=" + arrayList.get(i).getArrayList().get(j).getProduct_id());
                            //Log.i(TAG, "========>Cmp_prodId==>" + arrayList.get(pos).getArrayList().get(j).getProduct_id());
                            //Log.i(TAG, "========>prodId==>" + arrayList.get(i).getArrayList().get(j).getProduct_id());

                        } else {
                            isMatched = false;
                        }
                    }

                    if (isMatched) {

                        /*Log.i(TAG, "=============>Scheme  name=>" + arrayList.get(i).getScheme_name());
                        Log.i(TAG, "=========>Scheme   qty=>" + arrayList_total_scheme_order_list.get(i).getScheme_qty());
                        Log.i(TAG, "=========>Scheme price=>" + arrayList_total_scheme_order_list.get(i).getScheme_price());
                        Log.i(TAG, "=========>Scheme Value=>" + (Double.parseDouble(arrayList_total_scheme_order_list.get(i).getScheme_price()))
                                * Double.parseDouble((arrayList_total_scheme_order_list.get(i).getScheme_qty())));*/

                        tot_SchemeValue = tot_SchemeValue + (Double.parseDouble(arrayList_total_scheme_order_list.get(i).getScheme_price()))
                                * Double.parseDouble((arrayList_total_scheme_order_list.get(i).getScheme_qty()));
                    }

                } else {
                    //Log.i(TAG, "==>inside else not match no of prod");
                    isMatched = false;
                }
            }
        }

        return tot_SchemeValue;
    }

}
