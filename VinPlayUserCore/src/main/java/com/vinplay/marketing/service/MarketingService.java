package com.vinplay.marketing.service;

import com.vinplay.marketing.dao.UTMTrackingDAO;
import com.vinplay.marketing.dao.UserAccessLogDAO;
import com.vinplay.marketing.dao.MarketingUserDAO;
import com.vinplay.marketing.entity.MarketingUser;
import com.vinplay.marketing.entity.UTMTracking;
import com.vinplay.marketing.entity.UserAccessLog;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class MarketingService {
    private final UTMTrackingDAO utmTrackingDAO = new UTMTrackingDAO();
    private final MarketingUserDAO marketingUserDAO = new MarketingUserDAO();
    private final UserAccessLogDAO userAccessLogDAO = new UserAccessLogDAO();

    public UTMTracking getUTMTrackingByCampaign(String campaign) throws Exception {
        UTMTracking utmTracking = utmTrackingDAO.getUTMTrackingByCampaign(campaign);

        if (utmTracking == null) {
            throw new Exception("No UTMTracking found for campaign: " + campaign);
        }

        System.out.println("Found UTMTracking for campaign: " + campaign);
        System.out.println("Source: " + utmTracking.getUtmSource());
        System.out.println("Medium: " + utmTracking.getUtmMedium());
        System.out.println("Term: " + utmTracking.getUtmTerm());
        System.out.println("Content: " + utmTracking.getUtmContent());
        return utmTracking;
    }

    /**
     * Phương thức thứ 1: Thêm mới 1 chiến dịch marketing
     */
    public int addMarketingCampaign(String source, String medium, String campaign, String term, String content) throws Exception {
        UTMTracking utmTracking = new UTMTracking();
        utmTracking.setUtmSource(source);
        utmTracking.setUtmMedium(medium);
        utmTracking.setUtmCampaign(campaign);
        utmTracking.setUtmTerm(term);
        utmTracking.setUtmContent(content);
        utmTracking.setCreatedAt(LocalDateTime.now());
        utmTracking.setUpdatedAt(LocalDateTime.now());

        // Thêm chiến dịch marketing và trả về ID
        utmTrackingDAO.addUTMTracking(utmTracking);
        System.out.println("Added new marketing campaign: " + campaign);
        return utmTracking.getId();
    }

    /**
     * Phương thức thứ 2:
     * Bước 1: Tạo bản ghi user
     * Bước 2: Tạo bản ghi UserAccessLog
     */
    public void createUser(String userName, String userEmail, int utmId, String device, String browser) throws Exception {
        // Bước 1: Tạo bản ghi user và lấy lại đối tượng
        MarketingUser marketingUser = new MarketingUser();
        marketingUser.setName(userName);
        marketingUser.setEmail(userEmail);
        marketingUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        marketingUser.setUtmId(utmId);

        MarketingUser createdMarketingUser = marketingUserDAO.addUser(marketingUser); // Lấy User bao gồm ID
        System.out.println("Created new user: " + createdMarketingUser.getId() + " - " + createdMarketingUser.getName());
    }


    /**
     * Phương thức lưu trữ thông tin UserAccessLog
     *
     * @param userId ID của người dùng
     * @param utmId  ID của chiến dịch UTM
     * @param device Thiết bị truy cập
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lưu trữ
     */
    public void logUserAccess(long userId, int utmId, String device, LocalDateTime accessTime) throws Exception {
        // Kiểm tra xem user có tồn tại hay không
        MarketingUser marketingUser = marketingUserDAO.getUserById(userId);
        if (marketingUser == null) {
            throw new Exception("User with ID " + userId + " does not exist.");
        }

        // Tạo bản ghi UserAccessLog
        UserAccessLog accessLog = new UserAccessLog();
        accessLog.setUserId(userId);
        accessLog.setUtmId(utmId);
        accessLog.setDevice(device);
        accessLog.setBrowser("");
        accessLog.setAccessTime(accessTime);
        accessLog.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Lưu UserAccessLog vào cơ sở dữ liệu
        userAccessLogDAO.addUserAccessLog(accessLog);
    }

    /**
     * Phương thức lấy danh sách User theo tên
     *
     * @param name Tên của người dùng
     * @return người dùng có tên khớp
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lấy dữ liệu
     */
    public MarketingUser getUsersByName(String name) throws Exception {
        return marketingUserDAO.getUserByName(name);
    }

}
