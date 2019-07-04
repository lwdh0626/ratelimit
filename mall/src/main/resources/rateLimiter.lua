
---获取Java客户端传过来的参数(1个key, 2 个param)
---拿到我们的key，要限制的key-value  key
local key = KEYS[1];
---拿到限制的次数  10
local times = ARGV[1];
---拿到限制的时间段，秒  60
local expire = ARGV[2];
---对Redis进行操作 key 的value的+1操作,再判断
local val = redis.call('incr',key);
---第一次
if val == 1 then
    ---设置key的60秒有效期
    redis.call('expire',key,tonumber(expire))
    return 1;
end
---判断次数是否达到阈(yu)值,10,就要进行限制
if val>tonumber(times)  then
    return 0;
end
return 1;


