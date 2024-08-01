/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.cp;

import java.sql.SQLException;

public interface BaseProcessor<T, R> {
    public R execute(Param<T> var1) throws SQLException;
}

