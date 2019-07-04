
--令牌桶方案


--- 获取令牌
--- 返回码
--- 0 没有令牌桶配置
--- -1 表示取令牌失败，也就是桶里没有令牌
--- 1 表示取令牌成功
--- @param key 令牌的唯一标识
--- @param permits  请求令牌数量
--- @param curr_mill_second 当前毫秒数
--- @param max_permits 桶大小
--- @param rate  向桶里添加令牌的速率
local function acquire(key, permits, curr_mill_second,max_permits,rate)

    local rate_limit_info = redis.pcall("HMGET", key, "last_mill_second", "curr_permits", "max_permits", "rate")

    local last_mill_second = rate_limit_info[1]
    local curr_permits = tonumber(rate_limit_info[2])

    --如果当前没有初始化参数 则初始化
    if rate_limit_info == nil  then
         redis.pcall("HMSET", key, "max_permits", max_permits, "rate", rate, "curr_permits", max_permits)
         --设置当前令牌数=最大值
         curr_permits = max_permits;
    end

    local local_curr_permits = max_permits;

    --- 令牌桶刚刚创建，上一次获取令牌的毫秒数为空
    --- 根据和上一次向桶里添加令牌的时间和当前时间差，触发式往桶里添加令牌，并且更新上一次向桶里添加令牌的时间
    --- 如果向桶里添加的令牌数不足一个，则不更新上一次向桶里添加令牌的时间
    if (type(last_mill_second) ~= 'boolean'  and last_mill_second ~= nil) then
        local reverse_permits = math.floor(((curr_mill_second - last_mill_second) / 1000) * rate)
        local expect_curr_permits = reverse_permits + curr_permits;
        local_curr_permits = math.min(expect_curr_permits, max_permits);
        --- 大于0表示不是第一次获取令牌，也没有向桶里添加令牌
        if (reverse_permits > 0) then
            redis.pcall("HSET", key, "last_mill_second", curr_mill_second)
        end
    else
        redis.pcall("HSET", key, "last_mill_second", curr_mill_second)
    end

    local result = 0
    --如果当前令牌数-请求令牌数 》0 则访问成功
    if (local_curr_permits - permits >= 0) then
        result = 1
        redis.pcall("HSET", key, "curr_permits", local_curr_permits - permits)
    else
        redis.pcall("HSET", key, "curr_permits", local_curr_permits)
    end

    return result
end


local key = KEYS[1]
local permits = ARGV[1]
local currMillSecond = ARGV[2]
local max_permits = ARGV[3]
local rate = ARGV[4]

return acquire(key,permits, currMillSecond,max_permits,rate)