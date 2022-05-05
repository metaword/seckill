package com.aifurion.seckill.common.stockWithRedis;

import com.aifurion.seckill.common.utils.RedisPool;
import com.aifurion.seckill.common.utils.RedisPoolUtil;
import com.aifurion.seckill.pojo.Stock;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2022/5/5 15:25
 */

@Slf4j
public class StockWithRedis {

    /**
     * Redis 事务保证库存更新
     * 捕获异常后应该删除缓存
     */
    public static void updateRedisStock(Stock stock) {
        Jedis jedis = null;
        try {
            jedis = RedisPool.getJedis();
            // 开始事务
            Transaction transaction = jedis.multi();
            // 事务操作
            RedisPoolUtil.decr(RedisKeysConstant.STOCK_COUNT + stock.getId());
            RedisPoolUtil.incr(RedisKeysConstant.STOCK_SALE + stock.getId());
            RedisPoolUtil.incr(RedisKeysConstant.STOCK_VERSION + stock.getId());
            // 结束事务
            List<Object> list = transaction.exec();
        } catch (Exception e) {
            log.error("updateStock 获取 Jedis 实例失败：", e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
    }


}
