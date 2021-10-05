package com.jp.baxomdistributor.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.widget.ImageView;

import java.util.Locale;

public class PdfUtils {
    public static PdfDocument pdfDocument;
    public static Canvas canvas;
    public static Paint paint;
    public static PdfDocument.Page page;
    public static PdfDocument.PageInfo pageInfo;

    public static void initializeDoc() {

        pdfDocument = new PdfDocument();

    }

    public static PdfDocument getPdfDocument() {
        return pdfDocument;
    }


    public static PdfDocument.Page createPdfPage(int pageNumber, int pageWidth, int pageHeight) {
        //pdfDocument = new PdfDocument();
        paint = new Paint();
        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        page = pdfDocument.startPage(pageInfo);
        canvas = page.getCanvas();
        return page;
    }

    public static PdfDocument getDocument() {
        return pdfDocument;
    }

    public static PdfDocument.Page getPage() {
        return page;
    }

    public static Canvas getCanvas() {
        return canvas;
    }

    public static void setPaintBrush(int color, Paint.Align align, int size) {
        paint.setColor(color);
        paint.setTextAlign(align);
        paint.setTextSize(size);
        paint.setTextLocale(Locale.ENGLISH);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
    }

    public static void setPaintBrushNormal(int color, Paint.Align align) {
        paint.setColor(color);
        paint.setTextAlign(align);
        paint.setTextLocale(Locale.ENGLISH);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
    }

    public static void setPaintBrushNormal(int color, Paint.Align align, int size) {
        paint.setColor(color);
        paint.setTextAlign(align);
        paint.setTextLocale(Locale.ENGLISH);
        paint.setTextSize(size);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
    }

    public static void setPaintBrushBold(int color, Paint.Align align, int size) {
        paint.setColor(color);
        paint.setTextAlign(align);
        paint.setTextLocale(Locale.ENGLISH);
        paint.setTextSize(size);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    public static void setPaintBrushItalic(int color, Paint.Align align, int size) {
        paint.setColor(color);
        paint.setTextAlign(align);
        paint.setTextLocale(Locale.ENGLISH);
        paint.setTextSize(size);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
    }

    public static void setPaintBrushBoldItlic(int color, Paint.Align align, int size) {
        paint.setColor(color);
        paint.setTextAlign(align);
        paint.setTextLocale(Locale.ENGLISH);
        paint.setTextSize(size);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
    }

    public static void setPaintBrush(int color, Paint.Align align, int size, Paint.Style style) {
        paint.setColor(color);
        paint.setTextAlign(align);
        paint.setTextSize(size);
        paint.setTextLocale(Locale.ENGLISH);
        paint.setStyle(style);
    }

    public static void setPaintBrush(int color, Paint.Align align, int size, boolean underline) {
        paint.setColor(color);
        paint.setTextAlign(align);
        paint.setTextSize(size);
        paint.setTextLocale(Locale.ENGLISH);
        paint.setUnderlineText(underline);
    }

    public static void setPaintBrushWithDrawText(int color, Paint.Align align, int size, String data, int flotX, int flotY) {
        paint.setColor(color);
        paint.setTextAlign(align);
        paint.setTextSize(size);
        paint.setTextLocale(Locale.ENGLISH);
        drawText(data, flotX, flotY);
    }

    public static Paint getPaint() {
        return paint;
    }

    public static void drawText(String data, int flotX, int flotY) {
        getPaint().setStyle(Paint.Style.FILL);
        getPaint().setStrokeWidth(0);
        getCanvas().drawText(data, flotX, flotY, getPaint());
    }

    public static void drawText(String data, int flotX, int flotY, int rotate, int rflotX, int rflotY) {
        getCanvas().save();
        getPaint().setStyle(Paint.Style.FILL);
        getPaint().setStrokeWidth(0);
        getCanvas().rotate(rotate, rflotX, rflotY);
        getCanvas().drawText(data, flotX, flotY, getPaint());
        getCanvas().restore();
    }

    public static void drawText(String data, int start, int end, float x, float y) {
        getCanvas().drawText(data, start, end, x, y, getPaint());
    }

    public static void drawImage(Drawable drawable, int left, int top, int right, int bottom) {
        drawable.setBounds(left, top, right, bottom);
        drawable.draw(getCanvas());
    }

    public static void drawImage(Drawable drawable, int left, int top, int right, int bottom, int rotate, Context context) {
        ImageView imageView = new ImageView(context);
        /*drawable.setBounds(left, top, right, bottom);
        drawable.draw(getCanvas());*/
        drawable.setBounds(left, top, right, bottom);
        imageView.setRotation(rotate);
        imageView.setImageDrawable(drawable);
        imageView.draw(getCanvas());
    }

    public static void drawLine(float startX, float startY, float stopX, float stopY) {
        getPaint().setStyle(Paint.Style.STROKE);
        getPaint().setStrokeWidth(1);
        getCanvas().drawLine(startX, startY, stopX, stopY, getPaint());
    }

    public static void drawLines(float[] pts) {
        getCanvas().drawLines(pts, getPaint());
    }

    public static void drawLines(float[] pts, int ofset, int count) {
        getCanvas().drawLines(pts, ofset, count, getPaint());
    }

    public static void drawRect(Rect rect) {
        getCanvas().drawRect(rect, getPaint());
    }

    public static void drawRect(float left, float top, float right, float bottom) {
        getCanvas().drawRect(left, top, right, bottom, getPaint());
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        getPaint().setColor(color);
        getCanvas().drawRect(left, top, right, bottom, getPaint());
    }

    public static void drawRectF(RectF rect) {
        getCanvas().drawRect(rect, getPaint());
    }

    public static void drawRoundRect(RectF rect, float rx, float ry) {
        getCanvas().drawRoundRect(rect, rx, ry, getPaint());
    }

    public static void drawRoundRect(float left, float top, float right, float bottom, float rx, float ry) {
        getCanvas().drawRoundRect(left, top, right, bottom, rx, ry, getPaint());
    }

    public static void finishPage() {
        pdfDocument.finishPage(getPage());
    }


    public static void closePage() {
        pdfDocument.close();
    }

//    public static void makeTableWithData(int count, ArrayList<UserJob.DemandofRepair> arrayList_DemandofRepairs,int left,int top,int right,int bottom){
//        for (int i = 0; i < count; i++) {
//
//        }
//    }

}
