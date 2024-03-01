package com.vinplay.api.processors;

import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.LinkSocialResponse;

import javax.servlet.http.HttpServletRequest;

public class UpdateLinkSocialProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String fanPage = request.getParameter("fanPage");
        String groupFacebook = request.getParameter("groupFacebook");
        String teleCSKH = request.getParameter("teleCSKH");
        String botTele = request.getParameter("botTele");
        String groupTele = request.getParameter("groupTele");
        String liveChat = request.getParameter("liveChat");
        String linkDownload = request.getParameter("linkDownload");
        String home = request.getParameter("home");
        String chatId = request.getParameter("chatId");
        LinkSocialResponse linkSocialResponse = new LinkSocialResponse(true, "1001");
        linkSocialResponse.setFanPage(fanPage);
        linkSocialResponse.setGroupFacebook(groupFacebook);
        linkSocialResponse.setTeleCSKH(teleCSKH);
        linkSocialResponse.setBotTele(botTele);
        linkSocialResponse.setGroupTele(groupTele);
        linkSocialResponse.setLiveChat(liveChat);
        linkSocialResponse.setLinkDownload(linkDownload);
        linkSocialResponse.setHome(home);
        linkSocialResponse.setChatId(chatId);
        OtherServiceImpl otherService = new OtherServiceImpl();
        otherService.updateLinkSocial(linkSocialResponse);
        return otherService.getLinkSocial().toJson();
    }
}

