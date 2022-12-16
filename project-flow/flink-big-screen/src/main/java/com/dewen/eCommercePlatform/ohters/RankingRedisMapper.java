package com.dewen.eCommercePlatform.ohters;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


/**
 * @author dewen
 * @date 2022/12/6 16:31
 */
public final class RankingRedisMapper implements RedisMapper<Tuple2<Long, Long>> {
    private static final long serialVersionUID = 1L;
    private static final String ZSET_NAME_PREFIX = "RT:DASHBOARD:RANKING:";

    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.ZINCRBY, ZSET_NAME_PREFIX);
    }

    @Override
    public String getKeyFromData(Tuple2<Long, Long> data) {
        return String.valueOf(data.f0);
    }

    @Override
    public String getValueFromData(Tuple2<Long, Long> data) {
        return String.valueOf(data.f1);
    }

    @Override
    public Optional<String> getAdditionalKey(Tuple2<Long, Long> data) {
        return Optional.of(ZSET_NAME_PREFIX + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")) + ":MERCHANDISE");
    }
}
