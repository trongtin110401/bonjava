package com.vinplay.marketing.service;

import com.vinplay.marketing.dao.*;
import com.vinplay.marketing.entity.*;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.enums.UserAction;

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
            return;
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

    public void addUserServiceAndServiceLog(String nickname, String action, long value, LocalDateTime actionTime) {
        try {
            UserServiceImpl userService = new UserServiceImpl();
            String username = userService.getUser(nickname).getUsername();
            MarketingUser user = getUsersByName(username);
            if (user != null) {
                long marketingUserId = getUsersByName(username).getId();
                int serviceId = UserAction.getByName(action).getId();
                int utmId = user.getUtmId();

                UserService us = new UserService();
                us.setUserId(marketingUserId);
                us.setUtmId(utmId);
                us.setStartTime(actionTime);
                us.setServiceId(serviceId);

                System.out.println("=============> addUserServiceAndServiceLog 1: " + marketingUserId + " | " + serviceId + " | " + utmId);

                // add user service
                UserServiceDAO userServiceDAO = new UserServiceDAO();
                int userServiceId = userServiceDAO.addUserService(us);
                System.out.println("=============> addUserServiceAndServiceLog 2: user service id " + userServiceId);


                // add service log
                ServiceLog log = new ServiceLog();
                log.setUserServiceId(userServiceId);
                log.setAction(action);
                log.setActionTime(actionTime);
                log.setActionValue(value);

                ServiceLogDAO serviceLogDAO = new ServiceLogDAO();
                serviceLogDAO.addServiceLog(log);
            } else {
                System.out.println("=============> user action: " + nickname + " is null ");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
