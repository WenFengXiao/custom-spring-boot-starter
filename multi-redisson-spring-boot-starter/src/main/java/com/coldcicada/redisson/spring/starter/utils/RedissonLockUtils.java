package com.coldcicada.redisson.spring.starter.utils;

import com.coldcicada.redisson.spring.starter.context.RedissonContext;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @Description: redisson分布式锁工具类
 * @Author: coldcicada
 * @Date: 2024/6/25
 */
public class RedissonLockUtils {

    private static final String CLIENT_BEAN_SUFFIX = "RedissonClient";

    /**
     * @Description: 获取锁对象
     * @param lockKey
     * @param redissonClient
     * @Return org.redisson.api.RLock
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static RLock getLock(String lockKey, RedissonClient redissonClient) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * @Description: 加锁操作 如果已经被当前线程上锁则返回空 等待时间内没有拿到锁 返回空
     * @param lockKey
     * @param waitTime
     * @param expireTime
     * @param timeUnit
     * @param clientBeanPrefix
     * @Return java.util.Optional<org.redisson.api.RLock>
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static Optional<RLock> lock(String lockKey, long waitTime, long expireTime, TimeUnit timeUnit, String clientBeanPrefix) {

        try {

            RedissonClient redissonClient = RedissonContext.getRedissonClient(clientBeanPrefix + CLIENT_BEAN_SUFFIX);

            RLock rLock = redissonClient.getLock(lockKey);

            if (rLock.isHeldByCurrentThread()) {
                return Optional.empty();
            } else {
                return rLock.tryLock(waitTime, expireTime, timeUnit) ?  Optional.of(rLock) : Optional.empty();
            }

        } catch (Exception exception) {

            return Optional.empty();

        }
    }

    /**
     * @Description: 不会抛出异常的加锁操作 如果已经被当前线程上锁则返回空 等待时间内没有拿到锁 返回 empty Optional
     * @param lockKey
     * @param waitTime
     * @param expireTime
     * @param timeUnit
     * @param redissonClient
     * @Return java.util.Optional<org.redisson.api.RLock>
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static Optional<RLock> lock(String lockKey, long waitTime, long expireTime, TimeUnit timeUnit, RedissonClient redissonClient) {

        try {

            RLock rLock = redissonClient.getLock(lockKey);

            if (rLock.isHeldByCurrentThread()) {
                return Optional.empty();
            } else {
                return rLock.tryLock(waitTime, expireTime, timeUnit) ?  Optional.of(rLock) : Optional.empty();
            }

        } catch (Exception exception) {

            return Optional.empty();

        }
    }

    /**
     * @Description:  抛出异常的加锁操作
     * <p> 如果已经被当前线程上锁则返回空
     * <p> 等待时间内没有拿到锁 则抛出异常
     * @param lockKey
     * @param waitTime
     * @param expireTime
     * @param timeUnit
     * @param redissonClient
     * @Return java.util.Optional<org.redisson.api.RLock>
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static Optional<RLock> throwableLock(String lockKey, long waitTime, long expireTime, TimeUnit timeUnit, RedissonClient redissonClient){

        try {

            RLock rLock = redissonClient.getLock(lockKey);

            if (rLock.isHeldByCurrentThread()) {
                return Optional.empty();
            } else {
                if (rLock.tryLock(waitTime, expireTime, timeUnit)) {
                    return Optional.of(rLock);
                } else {
                    throw new RuntimeException("获取锁失败");
                }
            }
        } catch (Exception exception) {

            throw new RuntimeException("获取锁失败");

        }
    }


    /**
     * @Description: 抛出异常的加锁操作
     * <p> 如果已经被当前线程上锁则返回空
     * <p> 等待时间内没有拿到锁 则抛出异常
     * @param lockKey
     * @param waitTime
     * @param expireTime
     * @param timeUnit
     * @param clientBeanPrefix
     * @Return java.util.Optional<org.redisson.api.RLock>
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static Optional<RLock> throwableLock(String lockKey, long waitTime, long expireTime, TimeUnit timeUnit, String clientBeanPrefix){

        try {

            RedissonClient redissonClient = RedissonContext.getRedissonClient(clientBeanPrefix + CLIENT_BEAN_SUFFIX);

            RLock rLock = redissonClient.getLock(lockKey);

            if (rLock.isHeldByCurrentThread()) {
                return Optional.empty();
            } else {
                if (rLock.tryLock(waitTime, expireTime, timeUnit)) {
                    return Optional.of(rLock);
                } else {
                    throw new RuntimeException("获取锁失败");
                }
            }
        } catch (Exception exception) {

            throw new RuntimeException("获取锁失败");

        }
    }


    /**
     * @Description: 解锁 已上锁，且是被当前线程锁定 解锁，否则 不处理
     * @param lockKey
     * @param clientBeanPrefix
     * @Return void
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static void unlock(String lockKey, String clientBeanPrefix) {

        RedissonClient redissonClient = RedissonContext.getRedissonClient(clientBeanPrefix + CLIENT_BEAN_SUFFIX);

        RLock rLock = redissonClient.getLock(lockKey);

        if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
            rLock.unlock();
        }
    }

    /**
     * @Description: 解锁 已上锁，且是被当前线程锁定 解锁，否则 不处理
     * @param lockKey
     * @param redissonClient
     * @Return void
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static void unlock(String lockKey, RedissonClient redissonClient) {

        RLock rLock = redissonClient.getLock(lockKey);

        if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
            rLock.unlock();
        }
    }


    /**
     * @Description: 检查锁状态 true 已锁定 false 未锁定
     * @param lockKey
     * @param clientBeanPrefix
     * @Return boolean
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static boolean isLocked(String lockKey, String clientBeanPrefix) {

        RedissonClient redissonClient = RedissonContext.getRedissonClient(clientBeanPrefix + CLIENT_BEAN_SUFFIX);

        RLock rLock = redissonClient.getLock(lockKey);

        return rLock.isLocked();
    }


    /**
     * @Description: 检查锁状态 true 已锁定 false 未锁定
     * @param lockKey
     * @param redissonClient
     * @Return boolean
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static boolean isLocked(String lockKey, RedissonClient redissonClient) {
        RLock rLock = redissonClient.getLock(lockKey);

        return rLock.isLocked();
    }


    /**
     * @Description: 联锁上锁  返回空为上锁失败
     * @param lockKeys
     * @param waitTime
     * @param expireTime
     * @param timeUnit
     * @param redissonClient
     * @Return java.util.Optional<org.redisson.RedissonMultiLock>
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static Optional<RedissonMultiLock> multiLock(List<String> lockKeys, long waitTime, long expireTime, TimeUnit timeUnit, RedissonClient redissonClient) {

        RLock[] rLocks = new RLock[lockKeys.size()];

        Stream.iterate(0, i -> i + 1).limit(lockKeys.size()).forEach(i -> {

            RLock rLock = redissonClient.getLock(lockKeys.get(i));

            rLocks[i] = rLock;

        });

        RedissonMultiLock multiLock = new RedissonMultiLock(rLocks);

        try {

            if(multiLock.tryLock(waitTime,expireTime,timeUnit)) {
                return Optional.of(multiLock);
            }else {
                return Optional.empty();
            }

        } catch (Exception interruptedException) {

            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    /**
     * @Description: 线程释放
     * @param lockKey
     * @param clientBeanPrefix
     * @Return void
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static void countDown(String lockKey, String clientBeanPrefix){

        RedissonClient redissonClient = RedissonContext.getRedissonClient(clientBeanPrefix + CLIENT_BEAN_SUFFIX);

        RCountDownLatch latch = redissonClient.getCountDownLatch(lockKey);

        latch.countDown();
    }

    /**
     * @Description: 创建countDown并设置计数1
     * @param lockKey
     * @param clientBeanPrefix
     * @Return org.redisson.api.RCountDownLatch
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static RCountDownLatch getRCountDownLatch(String lockKey, String clientBeanPrefix) {

        RedissonClient redissonClient = RedissonContext.getRedissonClient(clientBeanPrefix + CLIENT_BEAN_SUFFIX);

        RCountDownLatch latch = redissonClient.getCountDownLatch(lockKey);

        latch.trySetCount(1);

        return latch;
    }

    /**
     * @Description: 创建 countDownLatch 并设置等待时间
     * @param lockKey
     * @param unit
     * @param waitTime
     * @param clientBeanPrefix
     * @Return boolean
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static boolean createCountDownLatchAndSetWaitTime(String lockKey, TimeUnit unit, int waitTime, String clientBeanPrefix) {

        RCountDownLatch latch = RedissonContext.getRedissonClient(clientBeanPrefix + CLIENT_BEAN_SUFFIX).getCountDownLatch(lockKey);

        latch.trySetCount(1);

        try {
            latch.await(waitTime,unit);

            if (latch.isExists()) {
                latch.countDown();
                return false;
            }else {
                return true;
            }
        } catch (InterruptedException e) {
            return false;
        }
    }

    private RedissonLockUtils() {
    }

}
