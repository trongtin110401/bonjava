/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.usercore.service.impl;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.LinkSocialResponse;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashMap;

public class OtherServiceImpl implements OtherService {
    @Override
    public LinkSocialResponse getLinkSocial() {
        LinkSocialResponse linkSocialResponse = new LinkSocialResponse(true, "0");

        HashMap<String, Object> conditions = new HashMap<>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("link_social");
        conditions.put("id", 1);
        FindIterable iterable = col.find(new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {
            public void apply(Document document) {
                linkSocialResponse.setId(1);
                linkSocialResponse.setBotTele(document.getString("bot_tele"));
                linkSocialResponse.setFanPage(document.getString("fan_page"));
                linkSocialResponse.setGroupFacebook(document.getString("group_facebook"));
                linkSocialResponse.setLiveChat(document.getString("live_chat"));
                linkSocialResponse.setTeleCSKH(document.getString("tele_cskh"));
                linkSocialResponse.setBotTele(document.getString("bot_tele"));
            }
        });
        return linkSocialResponse;
    }

    @Override
    public void updateLinkSocial(LinkSocialResponse response) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("link_social");
        Document filter = new Document("id", 1);
        Document document = new Document();
        document.put("bot_tele", response.getBotTele());
        document.put("fan_page", response.getFanPage());
        document.put("group_facebook", response.getGroupFacebook());
        document.put("live_chat", response.getLiveChat());
        document.put("tele_cskh", response.getTeleCSKH());
        document.put("bot_tele", response.getBotTele());
        Document update = new Document("$set", document);
        col.updateOne(filter, update);
    }
}

