package com.vinplay.secretcode.service;

import com.vinplay.bongda.entities.KeoBongDa;
import com.vinplay.secretcode.entity.UserSecretEntity;

public interface IUserSecretService {
    UserSecretEntity findCode(String username);

    boolean insertUserSecret(UserSecretEntity userSecret);

    boolean updateUserSecret(UserSecretEntity userSecret);
}
