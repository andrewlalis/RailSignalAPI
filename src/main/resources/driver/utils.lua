function tableLength(t)
    local c = 0
    for _ in pairs(t) do
        c = c + 1
    end
    return c
end

function startsWith(str, s)
    return str:find(s, 1, true) == 1
end

function readNum(validationFunction)
    local func = validationFunction or function (n)
        return n ~= nil
    end
    local num = nil
    while true do
        local s = io.read()
        if s ~= nil then num = tonumber(s) end
        if not func(num) then
            print("The number you entered is not valid.")
        else
            return num
        end
    end
end

function readStr(validationFunction)
    local func = validationFunction or function (s)
        return s ~= nil and string.len(s) > 0
    end
    while true do
        local str = io.read()
        if func(str) then
            return str
        else
            print("The string you entered is not valid.")
        end
    end
end
