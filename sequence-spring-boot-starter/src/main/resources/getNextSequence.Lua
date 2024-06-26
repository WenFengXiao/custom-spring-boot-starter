--[[
获取NEXT KEY脚本
KEYS 1 流水号类型 kind 在redis中的KEY
LUA脚本参数
ARGV[1] 当前日期

@return 9
serialization[1] = prefix       前缀
serialization[2] = cdate        当前日期
serialization[3] = cvalue       当前序号
serialization[4] = svalue       步长
serialization[5] = len          长度，不包含前缀和日期的长度，不足需补到指定的长度
serialization[6] = fchar        填充字符，长度不足时使用该字符补充长度。
serialization[7] = forient      补充规则,L:左边填充,R:右边填充
serialization[8] = prefetch     预取个数
]]--
--日期比较
local function dateCompare(date1,date2)
    local y1 = tonumber(string.sub(date1,1,4));
    local m1 = tonumber(string.sub(date1,5,6));
    local d1 = tonumber(string.sub(date1,7,8));

    local y2 = tonumber(string.sub(date2,1,4));
    local m2 = tonumber(string.sub(date2,5,6));
    local d2 = tonumber(string.sub(date2,7,8));

    if (y2 > y1) then
        return true;
    elseif (y2 < y1) then
        return false;
    else
        if(m2 > m1) then
            return true;
        elseif (m2 < m1) then
            return false;
        else
            if (d2 > d1) then
                return true;
            elseif(d2 < d1) then
                return false;
            else
                return false;
            end
        end
    end
end

local currentDate = ARGV[1];
local sequences = redis.call('HMGET', KEYS[1], 'PREFIX', 'CDATE', 'CVALUE', 'SVALUE', 'LEN', 'FCHAR', 'FORIENT', 'PREFETCH');
local cValue = sequences[3];
if(sequences[2] ~= currentDate and dateCompare(sequences[2], currentDate)) then
    redis.call('HMSET', KEYS[1], 'CDATE', currentDate, 'CVALUE', 0);
    cValue = 0;
end

redis.call('HINCRBY', KEYS[1], 'CVALUE', sequences[4] * sequences[8]);

sequences = redis.call('HMGET', KEYS[1], 'PREFIX', 'CDATE', 'CVALUE', 'SVALUE', 'LEN', 'FCHAR', 'FORIENT', 'PREFETCH');

if (sequences[8] == '1') then
    cValue = sequences[3];
end

return { sequences[1], sequences[2], cValue, sequences[4], sequences[5], sequences[6], sequences[7], sequences[8]};