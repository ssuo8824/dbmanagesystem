package com.dbmanagesys.manage.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class RedisService {
    
    @Autowired(required=false) //从spring中查找 bean
    private ShardedJedisPool shardedJedisPool;
    
    //通用模板
    public <T> T execute(Function<T, ShardedJedis> fun){
    	 ShardedJedis shardedJedis = null;
         try {
             // 从连接池中获取到jedis分片对象
             shardedJedis = shardedJedisPool.getResource();
             return fun.callback(shardedJedis);//!
         } finally {
             if (null != shardedJedis) {
                 // 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
                 shardedJedis.close();
             }
         }
    	
    }

    /**
     * 执行set操作
     * 
     * @param key
     * @param value
     * @return
     */
    public String set(  String key,  String value) {
       return execute(new Function<String, ShardedJedis>() {

		@Override
		public String callback(ShardedJedis e) {
			// TODO Auto-generated method stub
			return e.set(key, value);
		}
	});
    }
    
    /**
     * 执行get操作
     * 
     * @param key
     * @return
     */
    public String get(String key) {
    	return execute(new Function<String, ShardedJedis>() {

    		@Override
    		public String callback(ShardedJedis e) {
    			// TODO Auto-generated method stub
    			return e.get(key);
    		}
    	});
    }
    
    /**
     * 设置生存时间，单位为：秒
     * 
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(final String key, final Integer seconds) {
        return this.execute(new Function<Long, ShardedJedis>() {
            @Override
            public Long callback(ShardedJedis e) {
                return e.expire(key, seconds);
            }
        });
    }

    /**
     * 执行set操作并且设置生存时间，单位为：秒
     * 
     * @param key
     * @param value
     * @return
     */
    public String set(final String key, final String value, final Integer seconds) {
        return this.execute(new Function<String, ShardedJedis>() {
            @Override
            public String callback(ShardedJedis e) {
                String str = e.set(key, value);
                e.expire(key, seconds);
                return str;
            }
        });
    }
    
    
    /**
     * sha
     * @param key
     * @return
     */
    
    public Long del(String key){
    	return this.execute(new Function<Long, ShardedJedis>()
    	{

			@Override
			public Long callback(ShardedJedis e) {
					
				return e.del(key);
			}
		});
    	
    }
     
     
}
