package com.vinplay.marketing.service;

import com.vinplay.marketing.dao.*;
import com.vinplay.marketing.entity.*;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.enums.UserAction;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class MarketingService {
    private final UTMTrackingDAO utmTrackingDAO = new UTMTrackingDAO();
    private final MarketingUserDAO marketingUserDAO = new MarketingUserDAO();
    private final UserAccessLogDAO userAccessLogDAO = new UserAccessLogDAO();

    private final AgencyDAO agencyDAO = new AgencyDAO();

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
    public void createUser(String userName, String userEmail, int utmId, int agencyId, String device, String browser) throws Exception {
        // Bước 1: Tạo bản ghi user và lấy lại đối tượng
        MarketingUser marketingUser = new MarketingUser();
        marketingUser.setName(userName);
        marketingUser.setEmail(userEmail);
        marketingUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        marketingUser.setUtmId(utmId);
        marketingUser.setAgencyId(agencyId);

        marketingUserDAO.addUser(marketingUser); // Lấy User bao gồm ID
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

                // add user service
                UserServiceDAO userServiceDAO = new UserServiceDAO();
                int userServiceId = userServiceDAO.addUserService(us);

                // add service log
                ServiceLog log = new ServiceLog();
                log.setUserServiceId(userServiceId);
                log.setAction(action);
                log.setActionTime(actionTime);
                log.setActionValue(value);

                ServiceLogDAO serviceLogDAO = new ServiceLogDAO();
                serviceLogDAO.addServiceLog(log);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Add a new agency
    public void addAgency(String code, String name, int status) {
        try {
            // Check for duplicate code or other business validations
            if (isCodeDuplicate(code)) {
                throw new IllegalArgumentException("Agency code already exists: " + code);
            }
            agencyDAO.addAgency(code, name, status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add agency", e);
        }
    }

    // Retrieve an agency by ID
    public Agency getAgencyById(int id) {
        try {
            Agency agency = agencyDAO.getAgencyById(id);
            if (agency == null) {
                throw new IllegalArgumentException("Agency not found with ID: " + id);
            }
            return agency;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch agency by ID", e);
        }
    }

    // Retrieve an agency by code
    public Agency getAgencyByCode(String code) {
        try {
            Agency agency = agencyDAO.getAgencyByCode(code);
            if (agency == null) {
                throw new IllegalArgumentException("Agency not found with code: " + code);
            }
            return agency;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch agency by code", e);
        }
    }

    // Retrieve all agencies
    public List<Agency> getAllAgencies() {
        try {
            return agencyDAO.getAllAgencies();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all agencies", e);
        }
    }

    // Update an existing agency
    public void updateAgency(int id, String code, String name, int status) {
        try {
            Agency existingAgency = getAgencyById(id); // Ensure the agency exists
            if (existingAgency == null) {
                throw new IllegalArgumentException("Agency not found with ID: " + id);
            }

            // Check for duplicate code (only if the code is changing)
            if (!existingAgency.getCode().equals(code) && isCodeDuplicate(code)) {
                throw new IllegalArgumentException("Agency code already exists: " + code);
            }

            agencyDAO.updateAgency(id, code, name, status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update agency", e);
        }
    }

    // Delete an agency
    public void deleteAgency(int id) {
        try {
            Agency agency = getAgencyById(id); // Ensure the agency exists
            if (agency == null) {
                throw new IllegalArgumentException("Agency not found with ID: " + id);
            }
            agencyDAO.deleteAgency(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete agency", e);
        }
    }

    // Check if a code is already in use
    private boolean isCodeDuplicate(String code) {
        try {
            List<Agency> agencies = getAllAgencies();
            return agencies.stream().anyMatch(agency -> agency.getCode().equals(code));
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to validate agency code", e);
        }
    }
}
