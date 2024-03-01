/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.usercore.service;

import com.vinplay.vbee.common.response.LinkSocialResponse;
import org.bson.Document;

public interface OtherService {

    LinkSocialResponse getLinkSocial();

    void updateLinkSocial(LinkSocialResponse response);

    void saveTransactionUpdateFund(Document document);
}

