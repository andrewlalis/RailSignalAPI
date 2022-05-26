local VERSION = "1.0.2"

-- Global component states
local components = {}

-- Global signal-indexed state maps.
local lastTrainOverheadStates = {}
local lastTrainOverheadDataObjs = {}
local trainScanTimers = {}

-- Determines if a train is currently traversing the given signal.
local function trainOverhead(signal)
    return redstone.getInput(signal.redstoneInputSide)
end

-- Determines if a train was over the given signal the last time we checked.
local function wasTrainOverheadPreviously(signal)
    return lastTrainOverheadStates[signal.id] == true
end

local function getTrainData(signal)
    local det = peripheral.wrap(signal.detector)
    if det == nil then
        print("Error: Signal " .. signal.id .. "'s detector could not be connected. Please fix the config and restart.")
    else
        return det.consist()
    end
end

-- Returns the from, to branches for a signal when a train travels in the given direction.
local function getBranches(dir, signal)
    local branches = signal.branches
    if string.upper(branches[1].direction) == string.upper(dir) then
        return branches[2].id, branches[1].id
    elseif string.upper(branches[2].direction) == string.upper(dir) then
        return branches[1].id, branches[2].id
    end
end

-- Sends an update to the RailSignal API.
local function sendSignalUpdate(ws, signal, data, msgType)
    local from, to = getBranches(string.upper(data.direction), signal)
    local msg = textutils.serializeJSON({
        signalId = signal.id,
        fromBranchId = from,
        toBranchId = to,
        type = msgType
    })
    print("-> S: " .. signal.id .. ", from: " .. from .. ", to: " .. to .. ", T: " .. msgType)
    ws.send(msg)
end

local function updateBranchStatusIndicator(branch, status, config)
    if branch.monitor ~= nil then
        local mon = peripheral.wrap(branch.monitor)
        if mon ~= nil then
            local c = config.statusColors[status]
            if c == nil then c = config.statusColors.ERROR end
            mon.setBackgroundColor(c)
            mon.clear()
        else
            print("Error! Could not connect to monitor " .. branch.monitor .. " for branch " .. branch.id .. ". Check and fix config.")
        end
    end
end

local function updateBranchStatus(signal, branchId, status, config)
    for _, branch in pairs(signal.branches) do
        if branch.id == branchId then
            updateBranchStatusIndicator(branch, status, config)
            return
        end
    end
end

-- Manually set the status indicator for all branch monitors.
local function setAllBranchStatus(config, status)
    for _, signal in pairs(config.signals) do
        for _, branch in pairs(signal.branches) do
            updateBranchStatusIndicator(branch, status, config)
        end
    end
end

local function initMonitorColors(config)
    for _, signal in pairs(config.signals) do
        for _, branch in pairs(signal.branches) do
            if branch.monitor ~= nil then
                local mon = peripheral.wrap(branch.monitor)
                if mon ~= nil then
                    for c, v in pairs(config.paletteColors) do
                        mon.setPaletteColor(c, v)
                    end
                else
                    print("Error! Could not connect to monitor " .. branch.monitor .. " for branch " .. branch.id .. ". Check and fix config.")
                end
            end
        end
    end
end

-- Checks if all signals managed by this controller are reporting an "online" status.
local function checkSignalOnlineStatus(config)
    for _, signal in pairs(config.signals) do
        local resp = http.get(config.apiUrl .. "/railSystems/" .. config.rsId .. "/signals/" .. signal.id)
        if resp == nil or resp.getResponseCode() ~= 200 then
            return false
        else
            local signalData = textutils.unserializeJSON(resp.readAll())
            if not signalData.online then
                return false
            end
        end
    end
    return true
end

local function handleRedstoneEvent(ws, config)
    for _, signal in pairs(config.signals) do
        if trainOverhead(signal) and not wasTrainOverheadPreviously(signal) then
            local data = getTrainData(signal)
            if data == nil then
                print("Got redstone event but could not obtain train data on signal " .. signal.id)
            else
                lastTrainOverheadDataObjs[signal.id] = data
                lastTrainOverheadStates[signal.id] = true
                sendSignalUpdate(ws, signal, data, "BEGIN")
                trainScanTimers[signal.id] = os.startTimer(config.trainScanInterval)
            end
        end
    end
end

local function handleTrainScanTimerEvent(ws, config, timerId)
    for k, signal in pairs(config.signals) do
        if trainScanTimers[signal.id] == timerId then
            if trainOverhead(signal) then -- The train is still overhead.
                local data = getTrainData(signal)
                if data ~= nil then
                    lastTrainOverheadDataObjs[signal.id] = data
                end
                trainScanTimers[signal.id] = os.startTimer(config.trainScanInterval)
            else -- The train has left the signal so send an update.
                sendSignalUpdate(ws, signal, lastTrainOverheadDataObjs[signal.id], "END")
                lastTrainOverheadDataObjs[signal.id] = nil
                lastTrainOverheadStates[signal.id] = nil
                trainScanTimers[signal.id] = nil
            end
            return
        end
    end
    print("Warn: Train scan timer was ignored.")
end

local function handleWebSocketMessage(msg, config)
    local data = textutils.unserializeJSON(msg)
    local branchId = data["branchId"]
    local status = data["status"]
    print("<- B: " .. branchId .. ", Status: " .. status)
    for _, signal in pairs(config.signals) do
        updateBranchStatus(signal, branchId, status, config)
    end
end

local function loadConfig(file)
    local f = io.open(file, "r")
    if f == nil then return createConfig(file) end
    local text = f:read("*a")
    f:close()
    return textutils.unserialize(text)
end

-- Connects to the RailSignal API websocket. Will block indefinitely until a connection can be obtained.
local function connectToWebSocket(config)
    local signalIds = {}
    for _, signal in pairs(config.signals) do
        table.insert(signalIds, tostring(signal.id))
    end
    local signalIdsStr = table.concat(signalIds, ",")
    while true do
        local ws, err = http.websocket(config.wsUrl, {[config.wsHeader] = signalIdsStr})
        if ws == false then
            print("Error connecting to RailSignal websocket:\n\tError: " .. err .. "\n\tTrying again in 3 seconds.")
            os.sleep(3)
        else
            print("Successfully connected to RailSignal websocket at " .. config.wsUrl .. " for managing signals: " .. signalIdsStr)
            return ws
        end
    end
end

-- Main Script
term.clear()
print("RailSignal Signal Controller V" .. VERSION)
print("  By Andrew Lalis. For more info, check here: https://github.com/andrewlalis/RailSignalAPI")
print("  To update, execute \"sig update\" in the command line.")
local w, h = term.getSize()
print(string.rep("-", w))
if arg[1] ~= nil and string.lower(arg[1]) == "update" then
    print("Updating to the latest version of RailSignal signal program.")
    fs.delete("sig.lua")
    shell.execute("pastebin", "get", "erA3mSfd", "sig.lua")
    print("Download complete. Restarting...")
    os.sleep(1)
    os.reboot()
end
local config = loadConfig("sig_config.tbl")
initMonitorColors(config)
setAllBranchStatus(config, "NONE")
local ws = connectToWebSocket(config)
local refreshWebSocketAlarm = os.setAlarm((os.time() + math.random(1, 23)) % 24)
while true do
    local eventData = {os.pullEvent()}
    local event = eventData[1]
    if event == "redstone" then
        handleRedstoneEvent(ws, config)
    elseif event == "timer" then
        handleTrainScanTimerEvent(ws, config, eventData[2])
    elseif event == "websocket_message" then
        handleWebSocketMessage(eventData[3], config)
    elseif event == "websocket_closed" then
        setAllBranchStatus(config, "ERROR")
        print("! RailSignal websocket closed. Attempting to reconnect.")
        os.sleep(0.5)
        ws = connectToWebSocket(config)
    elseif event == "alarm" and eventData[2] == refreshWebSocketAlarm then
        print("! Checking signal online status.")
        if not checkSignalOnlineStatus(config) then
            print("Not all signals are reporting an online status. Reconnecting to the websocket.")
            ws.close()
            ws = connectToWebSocket(config)
        end
    end
end
ws.close()