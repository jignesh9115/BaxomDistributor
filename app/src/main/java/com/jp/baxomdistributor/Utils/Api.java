package com.jp.baxomdistributor.Utils;

import org.json.JSONArray;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Api {

    @FormUrlEncoded
    @POST("addneworder.php")
    Call<String> addNewOrder(@Field("orderdata") String orderdata);

    @GET("check_live_order.php")
    Call<String> checkLiveOrder(@Query("shop_id") String shop_id,
                                @Query("salesman_id") String salesman_id,
                                @Query("bit_id") String bit_id,
                                @Query("dist_id") String dist_id,
                                @Query("entry_date") String entry_date);

    @GET("add_temp_salesman_rights.php")
    Call<String> giveAnyTimeOrderPermission(@Query("parent_id") String parent_id,
                                            @Query("salesman_id") String salesman_id,
                                            @Query("right_name") String right_name,
                                            @Query("exp_time") String exp_time);

    @GET("delete_temp_salesman_rights.php")
    Call<String> removeTempPermission(@Query("temp_salesman_right_id") String temp_salesman_right_id);

    @GET("salesmanlist.php")
    Call<String> salesmanlistByparentId(@Query("salesman_id") String parent_id);

    @GET("update_salesman_imei_no.php")
    Call<String> updateIMEIno(@Query("salesman_id") String parent_id, @Query("device_id") String device_id);

    @GET("get_saleman_childlist_and_rights.php")
    Call<String> getSalesmanChildAndRights(@Query("salesman_id") String salesman_id);

    @GET("merge_bit.php")
    Call<String> bitlink(@Query("operation") String operation,
                         @Query("merge_bit_id") JSONArray jsonArray,
                         @Query("salesman_id") String salesman_id,
                         @Query("device_imei_id") String device_imei_id);

    @GET("unlinkBitSalesmans.php")
    Call<String> unlinkBitSalesman(@Query("bit_id") String bit_id,
                                   @Query("salesman_ids") JSONArray saleman_ids);

    @POST("get_monthly_target.php")
    Call<String> get_month_target(@Query("salesman_id") String salesman_id);

    @POST("add_salesman_target.php")
    Call<String> add_month_target(@Query("salesman_id") String salesman_id,
                                  @Query("verify_id") String verify_id,
                                  @Query("prev_month_1half") String prev_month_1half,
                                  @Query("prev_month_2half") String prev_month_2half,
                                  @Query("curr_month_1half") String curr_month_1half,
                                  @Query("curr_month_2half") String curr_month_2half,
                                  @Query("prev_total_call") String prev_total_call,
                                  @Query("curr_total_call") String curr_total_call,
                                  @Query("prev_month_old_call") String prev_month_old_call,
                                  @Query("prev_month_new_call") String prev_month_new_call,
                                  @Query("prev_month_tot_pc") String prev_month_tot_pc,
                                  @Query("curr_month_old_call") String curr_month_old_call,
                                  @Query("curr_month_new_call") String curr_month_new_call,
                                  @Query("curr_month_tot_pc") String curr_month_tot_pc,
                                  @Query("prev_month_old_line") String prev_month_old_line,
                                  @Query("prev_month_new_line") String prev_month_new_line,
                                  @Query("prev_month_tot_line") String prev_month_tot_line,
                                  @Query("curr_month_old_line") String curr_month_old_line,
                                  @Query("curr_month_new_line") String curr_month_new_line,
                                  @Query("curr_month_tot_line") String curr_month_tot_line,
                                  @Query("prev_group1_bussiness") String prev_group1_bussiness,
                                  @Query("prev_group2_bussiness") String prev_group2_bussiness,
                                  @Query("prev_tot_bussiness") String prev_tot_bussiness,
                                  @Query("curr_group1_bussiness") String curr_group1_bussiness,
                                  @Query("curr_group2_bussiness") String curr_group2_bussiness,
                                  @Query("curr_tot_bussiness") String curr_tot_bussiness,
                                  @Query("verify_status") String verify_status,
                                  @Query("target_prod_arr") JSONArray target_prod_arr);


    @POST("verify_salesman_target.php")
    Call<String> verify_month_target(@Query("salesman_id") String salesman_id,
                                     @Query("verify_id") String verify_id,
                                     @Query("target_id") String target_id,
                                     @Query("curr_month_old_call") String curr_month_old_call,
                                     @Query("curr_month_new_call") String curr_month_new_call,
                                     @Query("curr_month_tot_pc") String curr_month_tot_pc,
                                     @Query("curr_month_old_line") String curr_month_old_line,
                                     @Query("curr_month_new_line") String curr_month_new_line,
                                     @Query("curr_month_tot_line") String curr_month_tot_line,
                                     @Query("curr_group1_bussiness") String curr_group1_bussiness,
                                     @Query("curr_group2_bussiness") String curr_group2_bussiness,
                                     @Query("curr_tot_bussiness") String curr_tot_bussiness,
                                     @Query("verify_status") String verify_status,
                                     @Query("target_prod_arr") JSONArray jsonArray);

    @GET("get_my_target_data.php")
    Call<String> getMyTarget(@Query("salesman_id") String salesman_id,
                             @Query("month_val") String month_val,
                             @Query("year_val") String year_val);

    @GET("get_monitor_day_close_salesteam_filter.php")
    Call<String> getSalesFilterList(@Query("salesman_id") String salesman_id);

    @GET("salesmanListBydist.php")
    Call<String> getSalesmanListByDistId(@Query("dist_id") String dist_id,
                                         @Query("dates") String dates);

    @GET("undeliverOrderByDist.php")
    Call<String> getUndeliveredOrdersByDist(@Query("salesman_arr") JSONArray salesmans,
                                            @Query("dist_id") String dist_id);

    @GET("undeliverOrderByDist_v3.1.0.php")
    Call<String> getUndeliveredOrdersByDist_v3_1_0(@Query("salesman_arr") JSONArray salesmans,
                                                   @Query("dist_id") String dist_id);

    @GET("view_salesorder_pdf_by_group_dates_new.php")
    Call<String> getSalesOrderpdf_by_group_dates(@Query("dist_id") String dist_id,
                                                 @Query("salesdate_arr") String salesdate_arr,
                                                 @Query("sales_pdf_date_arr") String sales_pdf_date_arr);

    @GET("view_salesorder_pdf_by_group_dates_new_v3.1.0.php")
    Call<String> getSalesOrderpdf_by_group_dates_v3_1_0(@Query("dist_id") String dist_id,
                                                        @Query("salesdate_arr") String salesdate_arr,
                                                        @Query("sales_pdf_date_arr") String sales_pdf_date_arr);

    @GET("merge_shop_salesorderpdf_by_group_dates_new.php")
    Call<String> getMergeShop_SalesOrderpdf_by_group_dates(@Query("dist_id") String dist_id,
                                                           @Query("salesdate_arr") String salesdate_arr,
                                                           @Query("sales_pdf_date_arr") String sales_pdf_date_arr);

    @GET("merge_shop_salesorderpdf_by_group_dates_new_v3.1.0.php")
    Call<String> getMergeShop_SalesOrderpdf_by_group_dates_v3_1_0(@Query("dist_id") String dist_id,
                                                                  @Query("salesdate_arr") String salesdate_arr,
                                                                  @Query("sales_pdf_date_arr") String sales_pdf_date_arr);

    @GET("set_dist_pattern.php")
    Call<String> set_dist_pattern(@Query("dist_id") String dist_id,
                                  @Query("dates_arr") String salesdate_arr);

    @GET("viewsalesdeliveryorder.php")
    Call<String> view_sales_delivery_order(@Query("order_id") String dist_id);


    @GET("salesorder_delivery.php")
    Call<String> salesorder_delivery(@Query("orderdata") String orderdata);

    @GET("bussiness_summery_by_dist.php")
    Call<String> bussiness_summery_by_dist(@Query("dist_id") String dist_id,
                                           @Query("from_date") String from_date,
                                           @Query("to_date") String to_date);

    @GET("distributor_list.php")
    Call<String> get_distributor_list();

    @GET("get_distributor_stock_by_id.php")
    Call<String> get_distributor_stock_by_id(@Query("dist_id") String dist_id);

    @GET("get_distributor_scehem_stock_by_id.php")
    Call<String> get_distributor_scehem_stock_by_id(@Query("dist_id") String dist_id);

    @GET("distributor_stock_update.php")
    Call<String> update_distributor_stock();

    @GET("distributor_scheme_stock_update.php")
    Call<String> update_distributor_scheme_stock();

    @POST("distributor_stock_callibrate.php")
    Call<String> update_distributor_stock(@Query("dist_id") String dist_id,
                                          @Query("callibrate_prods_arr") JSONArray callibrate_prods_arr);

    @POST("distributor_scheme_stock_callibrate.php")
    Call<String> update_distributor_scheme_stock(@Query("dist_id") String dist_id,
                                                 @Query("callibrate_scheme_arr") JSONArray callibrate_prods_arr);

    @GET("load_order_against_sales.php")
    Call<String> load_order_against_sales(@Query("dist_id") String dist_id);

    @FormUrlEncoded
    @POST("store_sales_against_sales.php")
    Call<String> store_sales_against_sales(@Field("order_against_sales_arr") JSONArray order_against_sales_arr);

}

