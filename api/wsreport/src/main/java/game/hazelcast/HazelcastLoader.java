/*
 * Decompiled with CFR 0.144.
 */
package game.hazelcast;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class HazelcastLoader {
    @Value("${address}")
    String address;
    @Value("${group_name}")
    String group_name;
    @Value("${group_pass}")
    String group_pass;

    @Bean
    public void start() {
        HazelcastClientFactory.ADDRESS = address;
        HazelcastClientFactory.GROUP_NAME = group_name;
        HazelcastClientFactory.GROUP_PASS = group_pass;
        HazelcastClientFactory.initDefault();

    }
}

