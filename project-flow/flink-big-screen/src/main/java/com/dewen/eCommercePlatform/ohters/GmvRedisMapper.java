package com.dewen.eCommercePlatform.ohters;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public final class GmvRedisMapper implements RedisMapper<Tuple2<Long, String>> {
    private static final long serialVersionUID = 1L;
    private static final String HASH_NAME_PREFIX = "RT:DASHBOARD:GMV:";

    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.HSET, HASH_NAME_PREFIX);
    }

    @Override
    public String getKeyFromData(Tuple2<Long, String> data) {
        return String.valueOf(data.f0);
    }

    @Override
    public String getValueFromData(Tuple2<Long, String> data) {
        return data.f1;
    }

    @Override
    public Optional<String> getAdditionalKey(Tuple2<Long, String> data) {
        return Optional.of(HASH_NAME_PREFIX + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")) + ":SITES");
    }
}