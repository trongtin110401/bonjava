package com.vinplay.daily.entities;

public class UserWinLostResponse {
    public String NickName;
    public String UserName;
    public String CreateAt;
    public long TotalInUser;
    public long TotalOutUser;


    public UserWinLostResponse(String nickName , long totalInUser, long totalOutUser) {
        NickName = nickName;
        TotalInUser = totalInUser;
        TotalOutUser = totalOutUser;
    }

    public UserWinLostResponse(String nickName, String userName, String createAt) {
        NickName = nickName;
        UserName = userName;
        CreateAt = createAt;
    }

    public UserWinLostResponse(String nickName, String userName, String createAt, long totalInUser, long totalOutUser) {
        NickName = nickName;
        UserName = userName;
        CreateAt = createAt;
        TotalInUser = totalInUser;
        TotalOutUser = totalOutUser;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getCreateAt() {
        return CreateAt;
    }

    public void setCreateAt(String createAt) {
        CreateAt = createAt;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public long getTotalInUser() {
        return TotalInUser;
    }

    public void setTotalInUser(long TotalInUser) {
        this.TotalInUser = TotalInUser;
    }

    public long getTotalOutUser() {
        return TotalOutUser;
    }

    public void setTotalOutUser(long TotalOutUser) {
        this.TotalOutUser = TotalOutUser;
    }

}
