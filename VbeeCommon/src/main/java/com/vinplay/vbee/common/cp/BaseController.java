/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.cp;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.NoCommandRegistered;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseController<T, R> {
    private Map<Integer, BaseProcessor<T, R>> map = new HashMap<Integer, BaseProcessor<T, R>>();
    private static final Logger logger = Logger.getLogger((String)"api");

    public void initCommands(Map<Integer, String> commandMap) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (Integer command : commandMap.keySet()) {
            String path = commandMap.get(command);
            if (path == null) continue;
            try{
                Class<?> clazz = Class.forName(path);
                Constructor<?> ctor = clazz.getConstructor(new Class[0]);
                BaseProcessor processor = (BaseProcessor)ctor.newInstance(new Object[0]);
                this.map.put(command, processor);
            }
            catch (Exception ex){
                logger.info("BaseController:"+ex.getMessage());
            }
        }
    }

    public R processCommand(Integer command, Param<T> param) throws Exception {
        BaseProcessor<T, R> processor = this.map.get(command);
        if (processor == null) {
            throw new NoCommandRegistered("Command " + command + " not found");
        }
        return processor.execute(param);
    }
}

