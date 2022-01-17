package com.jp.baxomdistributor.Models;

import android.net.Uri;

/**
 * Created by Jignesh Chauhan on 11-01-2022
 */
public class DelFailReasonModel {
    String reason;
    boolean isselect;
    Uri uri;

    public DelFailReasonModel(String reason, boolean isselect, Uri uri) {
        this.reason = reason;
        this.isselect = isselect;
        this.uri = uri;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isIsselect() {
        return isselect;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
