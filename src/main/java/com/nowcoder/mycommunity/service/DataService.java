package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    // add the specified IP address to the UV
    public void recordUV(String ip){
        String redisKey = RedisKeyUtil.getUVKey(dateFormat.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    // query the specified range of days' UV
    public long calculateUV(Date start, Date end) {
        if(start == null || end == null){
            throw new IllegalArgumentException("start and end must not be null");
        }

        if(start.after(end)){
            throw new IllegalArgumentException("start date should be before end date");
        }

        // get all the keys of those days
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String redisKey = RedisKeyUtil.getUVKey(dateFormat.format(calendar.getTime()));
            keyList.add(redisKey);

            // add calendar to 1
            calendar.add(Calendar.DATE, 1);
        }

        // merge all the UV of those days
        String redisKey = RedisKeyUtil.getUVKey(dateFormat.format(start), dateFormat.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());

        // return the statistics result
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    // add the specified user to the DAU
    public void recordDAU(int userId){
        String redisKey = RedisKeyUtil.getDAUKey(dateFormat.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    // query the specified range of days' DAV
    public long calculateDAU(Date start, Date end){
        if(start == null || end == null){
            throw new IllegalArgumentException("start and end must not be null");
        }

        if(start.after(end)){
            throw new IllegalArgumentException("start date should be before end date");
        }

        // get all the keys of those days
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getDAUKey(dateFormat.format(calendar.getTime()));
            keyList.add(key.getBytes());

            // add calendar to 1
            calendar.add(Calendar.DATE, 1);
        }

        // or operation
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(dateFormat.format(start), dateFormat.format(end));
                connection.stringCommands().bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), keyList.toArray(new byte[0][0]));
                return connection.stringCommands().bitCount(redisKey.getBytes());
            }
        });
    }
}
