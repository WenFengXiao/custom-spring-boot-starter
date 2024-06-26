--[[
初始化流水号
KEYS[1] 流水号类型 kind 在redis中的KEY

LUA脚本参数
ARGV[1] 前缀
ARGV[2] 当前日期
ARGV[3] 当前值
ARGV[4] 步长
ARGV[5] 长度，不包含前缀和日期的长度，不足需补到指定的长度
ARGV[6] 填充字符，长度不足时使用该字符补充长度
ARGV[7] 补充规则,L:左边填充,R:右边填充
ARGV[8] 预取个数

@return 10
sequences[1] = prefix   前缀
sequences[2] = cdate    当前日期
sequences[3] = cvalue   起始序号
sequences[4] = svalue   步长
sequences[5] = len      长度，不包含前缀和日期的长度，不足需补到指定的长度
sequences[6] = fchar    填充字符，长度不足时使用该字符补充长度。
sequences[7] = forient  补充规则,L:左边填充,R:右边填充
sequences[8] = prefetch 预取个数
]]--

if(redis.call('EXISTS', KEYS[1])  == 0 )  then
    redis.call('HMSET', KEYS[1], 'PREFIX', ARGV[1], 'CDATE', ARGV[2], 'CVALUE', ARGV[3], 'SVALUE', ARGV[4], 'LEN', ARGV[5], 'FCHAR', ARGV[6], 'FORIENT', ARGV[7], 'PREFETCH', ARGV[8]);
else
    redis.call('HMSET', KEYS[1], 'PREFIX', ARGV[1], 'SVALUE', ARGV[4], 'LEN', ARGV[5], 'FCHAR', ARGV[6], 'FORIENT', ARGV[7], 'PREFETCH', ARGV[8]);
end

local sequences = redis.call('HMGET', KEYS[1], 'PREFIX', 'CDATE', 'CVALUE',  'SVALUE', 'LEN', 'FCHAR', 'FORIENT', 'PREFETCH');

return {sequences[1], sequences[2], tostring(sequences[3]), tostring(sequences[4]), tostring(sequences[5]),sequences[6],sequences[7], tostring(sequences[8])};