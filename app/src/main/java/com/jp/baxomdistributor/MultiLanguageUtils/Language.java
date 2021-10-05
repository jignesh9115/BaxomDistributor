package com.jp.baxomdistributor.MultiLanguageUtils;

import android.content.Context;

import com.jp.baxomdistributor.MultiLanguageUtils.EnglishUtils.CommanEng;
import com.jp.baxomdistributor.MultiLanguageUtils.GujaratiUtils.CommanGuj;
import com.jp.baxomdistributor.MultiLanguageUtils.HindiUtils.CommanHindi;

import java.util.ArrayList;


public class Language<T> {

    String langCode;
    Context context;
    ArrayList<String> arrayList_lang;

    public Language(String langCode, Context context, ArrayList<String> arrayList_lang) {
        this.langCode = langCode;
        this.context = context;
        this.arrayList_lang = arrayList_lang;
    }

    /*public T getLang() {
        Log.i("Language", "Count==>" + arrayList_lang.size());
        if (langCode.equalsIgnoreCase("ENG")) {
            return (T) new AddBitEnglish(arrayList_lang);
        } else {
            return (T) new AddBitGujarati(arrayList_lang);
        }
    }

    public T getShopLang() {
        Log.i("Language", "Count==>" + arrayList_lang.size());
        if (langCode.equalsIgnoreCase("ENG")) {
            return (T) new AddBitEnglish(arrayList_lang);
        } else {
            return (T) new AddShopGujarati(arrayList_lang);
        }
    }*/

    public T getCommanLang() {
        if (langCode.equalsIgnoreCase("ENG")) {
            return (T) new CommanEng(arrayList_lang);
        } else if (langCode.equalsIgnoreCase("GUJ")) {
            return (T) new CommanGuj(arrayList_lang);
        } else {
            return (T) new CommanHindi(arrayList_lang);
        }
    }

    /*public class Lang {
        public String addBit_titlebar_heading;
        public String addBit_bit_name_title;
        public String addBit_bit_name_hint;
        public String addBit_bit_desc_title;
        public String addBit_bit_desc_hint;
        public String addBit_bit_target_title;
        public String addBit_bit_target_hint;
        public String addBit_distributor_txt_title;
        public String addBit_btn_submit_title;

        public Lang(String addBit_titlebar_heading, String addBit_bit_name_title, String addBit_bit_name_hint, String addBit_bit_desc_title, String addBit_bit_desc_hint, String addBit_bit_target_title, String addBit_bit_target_hint, String addBit_distributor_txt_title, String addBit_btn_submit_title) {
            this.addBit_titlebar_heading = addBit_titlebar_heading;
            this.addBit_bit_name_title = addBit_bit_name_title;
            this.addBit_bit_name_hint = addBit_bit_name_hint;
            this.addBit_bit_desc_title = addBit_bit_desc_title;
            this.addBit_bit_desc_hint = addBit_bit_desc_hint;
            this.addBit_bit_target_title = addBit_bit_target_title;
            this.addBit_bit_target_hint = addBit_bit_target_hint;
            this.addBit_distributor_txt_title = addBit_distributor_txt_title;
            this.addBit_btn_submit_title = addBit_btn_submit_title;
        }
    }

    public class ShopLang {

        public String addShop_titlebar_heading;
        public String addShop_shop_name_title;
        public String addShop_shop_name_hint;
        public String addShop_shop_address_title;
        public String addShop_shop_address_hint;
        public String addShop_contact_person_name_title;
        public String addShop_contact_person_name_hint;
        public String addShop_contact1_title;
        public String addShop_contact1_hint;
        public String addShop_contact2_title;
        public String addShop_contact2_hint;
        public String addShop_shop_category_title;
        public String addShop_invoice_type_title;
        public String addShop_gstno_title;
        public String addShop_gstno_hint;
        public String addShop_btn_verifygst_title;
        public String addShop_btn_submit_title;

        public ShopLang(String addShop_titlebar_heading, String addShop_shop_name_title, String addShop_shop_name_hint, String addShop_shop_address_title, String addShop_shop_address_hint, String addShop_contact_person_name_title, String addShop_contact_person_name_hint, String addShop_contact1_title, String addShop_contact1_hint, String addShop_contact2_title, String addShop_contact2_hint, String addShop_shop_category_title, String addShop_invoice_type_title, String addShop_gstno_title, String addShop_gstno_hint, String addShop_btn_verifygst_title, String addShop_btn_submit_title) {
            this.addShop_titlebar_heading = addShop_titlebar_heading;
            this.addShop_shop_name_title = addShop_shop_name_title;
            this.addShop_shop_name_hint = addShop_shop_name_hint;
            this.addShop_shop_address_title = addShop_shop_address_title;
            this.addShop_shop_address_hint = addShop_shop_address_hint;
            this.addShop_contact_person_name_title = addShop_contact_person_name_title;
            this.addShop_contact_person_name_hint = addShop_contact_person_name_hint;
            this.addShop_contact1_title = addShop_contact1_title;
            this.addShop_contact1_hint = addShop_contact1_hint;
            this.addShop_contact2_title = addShop_contact2_title;
            this.addShop_contact2_hint = addShop_contact2_hint;
            this.addShop_shop_category_title = addShop_shop_category_title;
            this.addShop_invoice_type_title = addShop_invoice_type_title;
            this.addShop_gstno_title = addShop_gstno_title;
            this.addShop_gstno_hint = addShop_gstno_hint;
            this.addShop_btn_verifygst_title = addShop_btn_verifygst_title;
            this.addShop_btn_submit_title = addShop_btn_submit_title;
        }
    }

    public Lang getAddBitData() {

        if (langCode.equalsIgnoreCase("ENG")) {

            AddBitEnglish english = (AddBitEnglish) getLang();
            return new Lang(english.addBit_titlebar_heading,
                    english.addBit_bit_name_title,
                    english.addBit_bit_name_hint,
                    english.addBit_bit_desc_title,
                    english.addBit_bit_desc_hint,
                    english.addBit_bit_target_title,
                    english.addBit_bit_target_hint,
                    english.addBit_distributor_txt_title,
                    english.addBit_btn_submit_title);

        } else {

            AddBitGujarati gujarati = (AddBitGujarati) getLang();
            return new Lang(gujarati.addBit_titlebar_heading,
                    gujarati.addBit_bit_name_title,
                    gujarati.addBit_bit_name_hint,
                    gujarati.addBit_bit_desc_title,
                    gujarati.addBit_bit_desc_hint,
                    gujarati.addBit_bit_target_title,
                    gujarati.addBit_bit_target_hint,
                    gujarati.addBit_distributor_txt_title,
                    gujarati.addBit_btn_submit_title);

        }
    }


    public ShopLang getAddShopData() {

        if (langCode.equalsIgnoreCase("ENG")) {

            AddShopEnglish english = (AddShopEnglish) getShopLang();

            return new ShopLang(english.addShop_titlebar_heading,
                    english.addShop_shop_name_title,
                    english.addShop_shop_name_hint,
                    english.addShop_shop_address_title,
                    english.addShop_shop_address_hint,
                    english.addShop_contact_person_name_title,
                    english.addShop_contact_person_name_hint,
                    english.addShop_contact1_title,
                    english.addShop_contact1_hint,
                    english.addShop_contact2_title,
                    english.addShop_contact2_hint,
                    english.addShop_shop_category_title,
                    english.addShop_invoice_type_title,
                    english.addShop_gstno_title,
                    english.addShop_gstno_hint,
                    english.addShop_btn_verifygst_title,
                    english.addShop_btn_submit_title);
        } else {


            AddShopGujarati gujarati = (AddShopGujarati) getShopLang();

            return new ShopLang(gujarati.addShop_titlebar_heading,
                    gujarati.addShop_shop_name_title,
                    gujarati.addShop_shop_name_hint,
                    gujarati.addShop_shop_address_title,
                    gujarati.addShop_shop_address_hint,
                    gujarati.addShop_contact_person_name_title,
                    gujarati.addShop_contact_person_name_hint,
                    gujarati.addShop_contact1_title,
                    gujarati.addShop_contact1_hint,
                    gujarati.addShop_contact2_title,
                    gujarati.addShop_contact2_hint,
                    gujarati.addShop_shop_category_title,
                    gujarati.addShop_invoice_type_title,
                    gujarati.addShop_gstno_title,
                    gujarati.addShop_gstno_hint,
                    gujarati.addShop_btn_verifygst_title,
                    gujarati.addShop_btn_submit_title);

        }

    }*/


    public class CommanList {

        ArrayList<String> arrayList;

        public CommanList(ArrayList<String> arrayList) {
            this.arrayList = arrayList;
        }

        public ArrayList<String> getArrayList() {
            return arrayList;
        }
    }


    public CommanList getData() {

        if (langCode.equalsIgnoreCase("ENG")) {
            CommanEng commanEng = (CommanEng) getCommanLang();
            return new CommanList(commanEng.getArrayList());
        } else if (langCode.equalsIgnoreCase("GUJ")) {
            CommanGuj commanGuj = (CommanGuj) getCommanLang();
            return new CommanList(commanGuj.getArrayList());
        } else {
            CommanHindi commanHindi = (CommanHindi) getCommanLang();
            return new CommanList(commanHindi.getArrayList());

        }

    }


}
