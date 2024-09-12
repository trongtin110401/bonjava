/*
 * Decompiled with CFR 0_116.
 *
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package bitzero.util.common.business;

import bitzero.server.config.ConfigHandle;

import java.io.PrintStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

public class Debug {
    public static boolean DEBUG = ConfigHandle.instance().getLong("debug_trace") == 1;
    public static Logger log = Logger.getLogger((String) "debug");

    public static /* varargs */ void trace(Object... objs) {
        StringBuilder str = new StringBuilder();
        String separator = " ";
        for (Object o : objs) {
            str.append(separator).append(o);
        }
        log.debug((Object) str.toString());
    }

    public static /* varargs */ void info(Object... objs) {
        StringBuilder str = new StringBuilder();
        String separator = " | ";
        for (Object o : objs) {
            str.append(separator).append(o);
        }
        log.info((Object) str.toString());
    }

    public static /* varargs */ void warn(Object... objs) {
        StringBuilder str = new StringBuilder();
        String separator = " | ";
        for (Object o : objs) {
            str.append(separator).append(o);
        }
        log.warn((Object) str.toString());
    }

    public static void printStackTrace() {
        StackTraceElement[] objs = Thread.currentThread().getStackTrace();
        StringBuilder str = new StringBuilder();
        String separator = "\n";
        for (StackTraceElement o : objs) {
            str.append(separator).append(o);
        }
        Debug.trace((Object) str.toString());
    }

    public static /* varargs */ void system(Object... objs) {
        StringBuilder str = new StringBuilder();
        String separator = " | ";
        for (Object o : objs) {
            str.append(separator).append(o);
        }
        log.fatal((Object) str.toString());
    }

    public static void trace(Object s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    public static void error(Exception ex) {
        log.error(ex.getMessage(), ex);
    }
}

