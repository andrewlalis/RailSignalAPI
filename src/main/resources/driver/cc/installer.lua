local function tableLength(t)
  local c = 0
  for _ in pairs(t) do
    c = c + 1
  end
  return c
end

local function startsWith(str, s)
  return str:find(s, 1, true) == 1
end

local function readNum(validationFunction)
  local func = validationFunction or function (n)
    return n ~= nil, "Please enter a valid number."
  end
  local num = nil
  while true do
    local s = io.read()
    if s ~= nil then num = tonumber(s) end
    local valid, msg = func(num)
    if valid then return num else print(msg) end
  end
end

local function readNumInRange(s, e)
  return readNum(function (n)
    return n ~= nil and n >= s and n <= e, "Please enter a number between " .. s .. " and " .. e .. "."
  end)
end

local function readStr(validationFunction)
  local func = validationFunction or function (s)
    return s ~= nil and string.len(s) > 0, "Please enter a non-empty string."
  end
  while true do
    local str = io.read()
    local valid, msg = func(str)
    if valid then return str else print(msg) end
  end
end

local function readUrl()
  return readStr(function (s)
    return s ~= nil and (startsWith(s, "http://") or startsWith(s, "https://")), "Please enter a valid URL."
  end)
end

local function choice(options, required)
  local req = required or false
  local maxChoices = tableLength(options)
  for i = 1, maxChoices do
    local text = options[i].text or options[i]
    print("[" .. i .. "] " .. text)
  end

  if not req then
    maxChoices = maxChoices + 1
    print("[" .. maxChoices .. "] None")
  end
  local c = readNumInRange(1, maxChoices)
  if not req and c == maxChoices then
    return nil
  else
    local value = options[c].value or options[c]
    return value
  end
end

local function chooseBoolean()
  return choice({"true", "false"}, true) == "true"
end

local function fetchJson(url)
  local response = http.get(url)
  if response then
    local text = response.readAll()
    response.close()
    return textutils.unserialiseJSON(text)
  else
    return nil
  end
end

local function chooseRsSide()
  return choice({"front", "back", "left", "right", "top", "bottom"}, true)
end

local function choosePeripheral(prefix, blacklist)
  local ps = peripheral.getNames()
  local choices = {}
  for _, name in pairs(ps) do
    if startsWith(name, prefix) then
      local isBanned = false
      for _, bannedName in pairs(blacklist) do
        if name == bannedName then
          isBanned = true
          break
        end
      end
      if not isBanned then table.insert(choices, name) end
    end
  end
  if tableLength(choices) < 1 then
    return nil
  end
  return choice(choices, true)
end

local function configSegmentBoundary(data, sb)
  print("What side is the redstone input for this segment boundary?")
  data.rsSide = chooseRsSide()
  print("Select the detector augment that this segment boundary will use.")
  data.augmentId = choosePeripheral("ir_augment_detector", {})
  data.segments = {}
  for _, segment in pairs(sb.segments) do
    local segmentData = {id = segment.id}
    print("In what direction would a train travel towards the segment \"" .. segment.name .. "\"?")
    segmentData.direction = string.upper(choice({"North", "South", "East", "West"}, true))
    table.insert(data.segments, segmentData)
  end
end

local function configSignal(data, signal)
  print("Select the monitor that this signal will use.")
  local monitorId = choosePeripheral("monitor", {})
  data.segment = {
    id = signal.segment.id,
    monitorId = monitorId
  }
end

local function configSwitch(data, switch)
  print("What side is the redstone input/output for this switch?")
  data.rsSide = chooseRsSide()
  data.possibleConfigurations = {}
  for _, cfg in pairs(switch.possibleConfigurations) do
    local cfgData = {id = cfg.id}
    local names = {}
    for _, node in pairs(cfg.nodes) do table.insert(names, node.name) end
    local routeName = table.concat(names, ", ")
    print("What is the redstone output to configure the switch to send traffic via " .. routeName)
    cfgData.rsOutput = chooseBoolean()
    table.insert(data.possibleConfigurations, cfgData)
  end
end

-- SCRIPT START

local config = {}
local args = {...}
print("Rail Signal Driver Installer for CC:Tweaked")
print("-------------------------------------------")
if #args < 1 then
  print("Error: Missing required baseURL argument.")
  return
end
local baseUrl = args[1]
config.apiUrl = baseUrl .. "/api"
local statusResponse = http.get(config.apiUrl .. "/status")
if not statusResponse then
  print("Error: Could not reach the the Rail Signal system at " .. config.apiUrl .. "/status")
  return
end
if startsWith(baseUrl, "https") then
  config.wsUrl = "wss" .. string.sub(baseUrl, 6) .. "/api/ws/component"
else
  config.wsUrl = "ws" .. string.sub(baseUrl, 5) .. "/api/ws/component"
end
print("Please enter this device's link token.")
config.linkToken = readStr()
local tokenData = fetchJson(config.apiUrl .. "/lt/" .. config.linkToken)
if tokenData == nil then
  print("Error: Could not fetch data for this token. Please make sure your token and URL is correct.")
  return
end
print("The token you entered is for the \"" .. tokenData.rsName .. "\" rail system, and controls " .. tableLength(tokenData.components) .. " components:")
for _, component in pairs(tokenData.components) do
  print("\t" .. component.name .. "\n\t\tID: " .. component.id .. ", Type: " .. component.type)
end
print("Are you sure you want to continue?\n[1] Continue\n[2] Exit and try again")
local c = readNumInRange(1, 2)
if c == 2 then
  return
end

config.components = {}
for _, component in pairs(tokenData.components) do
  print("Configuring component " .. component.name .. ":")
  print("-----------------------------------------------")
  local componentData = {
    id = component.id,
    type = component.type
  }
  if component.type == "SEGMENT_BOUNDARY" then
    configSegmentBoundary(componentData, component)
  elseif component.type == "SIGNAL" then
    configSignal(componentData, component)
  elseif component.type == "SWITCH" then
    configSwitch(componentData, component)
  end
  table.insert(config.components, componentData)
end

print("How often should segment boundaries perform train scans? Give an interval between 0.1 and 3 seconds.")
config.trainScanInterval = readNumInRange(0.1, 3)

print("Configuration complete!")
local configFile = io.open("__rs_config.tbl", "w")
configFile:write(textutils.serialize(config))
configFile:close()
print("Saved config.")

print("Downloading driver...")
local resp = http.get(baseUrl .. "/driver/cc/driver.lua")
local driverScript = resp.readAll()
resp.close()
local driverFile = io.open("rs_driver.lua", "w")
driverFile:write(driverScript)
driverFile:close()
print("Downloaded driver file.")

local startupFile = io.open("startup.lua", "w")
startupFile:write("shell.execute(\"rs_driver.lua\", \"__rs_config.tbl\")")
startupFile:close()
print("Created startup file.")

print("Rebooting to start the device...")
os.sleep(1)
os.reboot()
