package com.nowcoder.mycommunity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey, 1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashes() {
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "qjl");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    @Test
    public void testLists() {
        String redisKey = "list:ids";

        redisTemplate.opsForList().leftPush(redisKey, "id");
        redisTemplate.opsForList().leftPush(redisKey, "name");

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 1));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));
    }

    @Test
    public void testSortedSets() {
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey, "qjl", 80);
        redisTemplate.opsForZSet().add(redisKey, "zhj", 90);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "qjl"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "qjl"));
    }

    @Test
    public void testKeys() {
        redisTemplate.delete("test:user");

        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:student", 10, TimeUnit.SECONDS);
    }

    // if you query the same key multiple times
    @Test
    public void testBoundOperation() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

        operations.increment();
        operations.increment();
        operations.increment();

        System.out.println(operations.get());
    }

    @Test
    public void testTransactional() {
        Object object = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:key";

                operations.multi();

                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");
                operations.opsForSet().add(redisKey, "qjl");
                operations.opsForSet().add(redisKey, "zhj");

                System.out.println(operations.opsForSet().members(redisKey));

                return operations.exec();
            }
        });
        System.out.println(object);
    }

    @Test
    public void testHyperLogLog(){
        String redisKey = "test:hll:01";
        redisTemplate.delete(redisKey);

        for(int i = 1; i <= 10000; ++ i){
            redisTemplate.opsForHyperLogLog().add(redisKey, i);
        }

        String redisKey2 = "test:hll:02";
        redisTemplate.delete(redisKey2);
        for(int i = 5001; i <= 15000; ++ i){
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }

        long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);

        String redisKey3 = "test:hll:03";
        redisTemplate.delete(redisKey3);
        redisTemplate.opsForHyperLogLog().union(redisKey3, redisKey2, redisKey);
        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey3));
    }

    @Test
    public void testBitMap(){
        String redisKey = "test:bm:01";
        redisTemplate.delete(redisKey);

        redisTemplate.opsForValue().setBit(redisKey, 1, true);
        redisTemplate.opsForValue().setBit(redisKey, 4, true);
        redisTemplate.opsForValue().setBit(redisKey, 7, true);

        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 4));

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.stringCommands().bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);

        String redisKey2 = "test:bm:02";
        redisTemplate.delete(redisKey2);
        redisTemplate.opsForValue().setBit(redisKey2, 2, true);
        redisTemplate.opsForValue().setBit(redisKey2, 4, true);
        redisTemplate.opsForValue().setBit(redisKey2, 9, true);

        String redisKey3 = "test:bm:03";
        redisTemplate.delete(redisKey3);
        Object obj2 = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.stringCommands().bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey3.getBytes(), redisKey2.getBytes(), redisKey.getBytes());
                return connection.stringCommands().bitCount(redisKey3.getBytes());
            }
        });
        System.out.println(obj2);
    }
}
