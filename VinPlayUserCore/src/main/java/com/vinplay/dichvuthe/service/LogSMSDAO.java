/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinplay.dichvuthe.service;

/**
 *
 * @author HA
 */
public interface LogSMSDAO {
    public boolean saveLog(String request_id, String mobile, String message, String status, String transTime, String command_code);
}
