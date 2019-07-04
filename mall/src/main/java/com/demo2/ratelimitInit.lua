
-- [[从第一次初始化的参数中获取
			last_mill_second 最后时间毫秒
            curr_permits 当前可用的令牌
            max_burst 令牌桶最大值
            rate 每秒生成几个令牌
            app 应用接口名称

			ARGV[1]  每次请求消耗令牌数
			ARGV[2]  当前redis时间
--]]

local ratelimit_info=redis.pcall("HMGET",KEYS[1],"last_mill_second","curr_permits","max_burst","rate","app")
local last_mill_second=ratelimit_info[1]
local curr_permits=tonumber(ratelimit_info[2])
local max_burst=tonumber(ratelimit_info[3])
local rate=tonumber(ratelimit_info[4])
local app=tostring(ratelimit_info[5])

--判断app是否存在--
if app == nil then
    return 0
end

local local_curr_permits=max_burst;

if(type(last_mill_second) ~='boolean' and last_mill_second ~=nil) then
	-- 向下取整函数  计算应生成令牌数--
    local reverse_permits=math.floor((ARGV[2]-last_mill_second)/1000)*rate

    if(reverse_permits>0) then
        redis.pcall("HMSET",KEYS[1],"last_mill_second",ARGV[2])
    end
	-- 期望生成的令牌数--
    local expect_curr_permits=reverse_permits+curr_permits
	-- 与最大值比较取最小值--
    local_curr_permits=math.min(expect_curr_permits,max_burst);
else
    redis.pcall("HMSET",KEYS[1],"last_mill_second",ARGV[2])
end

local result=-1
-- 判断当前令牌数 是否够用--
if(local_curr_permits-ARGV[1]>0) then
    result=1
	--刷新当前令牌数--
    redis.pcall("HMSET",KEYS[1],"curr_permits",local_curr_permits-ARGV[1])
else
    redis.pcall("HMSET",KEYS[1],"curr_permits",local_curr_permits)
end

return result
