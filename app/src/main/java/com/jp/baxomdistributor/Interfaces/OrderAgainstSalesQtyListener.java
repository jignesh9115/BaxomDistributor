package com.jp.baxomdistributor.Interfaces;

/**
 * Created by Jignesh Chauhan on 01-01-2022
 */
public interface OrderAgainstSalesQtyListener {
    void qtyChange(int pos, double scheme_qty, double replace_qty, double shortage_qty);
}
