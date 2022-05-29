-- Rail Signal CC:Tweaked Driver
local VERSION = "1.0.0"

local args = {...}

-- Global config. Will be loaded at start of script.
local config = {}
-- Global websocket reference
local ws = nil

-- Loads config from a given filename and returns the table with data.
local function loadConfig(filename)
  local configFile = io.open(filename, "r")
  if not configFile then
    return nil
  end
  local txt = configFile:read("*a")
  local cfg = textutils.unserialize(txt)
  configFile:close()
  return cfg
end

-- Fetches JSON data from the given endpoint, using the config's API url.
local function fetchJson(endpoint)
  local response, msg, r = http.get({
    url = config.apiUrl .. endpoint,
    method = "GET"
  })
  if response then
    return textutils.unserialiseJSON(response.readAll())
  else
    print("Error: " .. r.getResponseCode())
    return nil
  end
end

-- Blocking function to obtain a websocket connection to Rail Signal.
local function connectToWebsocket()
  local url = config.wsUrl .. "?token=" .. config.linkToken
  print("Connecting to Rail Signal websocket...")
  while true do
    local ws, msg = http.websocket(url)
    if ws == false then
      print("Error connecting to the Rail Signal API websocket:\n\tError: " .. msg .. "\n\tTrying again in 3 seconds.")
      os.sleep(3)
    else
      print("Successfully connected to Rail Signal API websocket.")
      return ws
    end
  end
end

local function getComponentById(id)
  for _, c in pairs(config.components) do
    if c.id == id then
      return c
    end
  end
  return nil
end

local function displaySignal(signal)
  local color = colors.white
  if signal.segment.occupied ~= nil then
    if signal.segment.occupied then
      color = colors.red
    else
      color = colors.lime
    end
  end
  signal.segment.monitor.setBackgroundColor(color)
  signal.segment.monitor.clear()
end

local function setSwitchActiveConfig(switch, configId)
  for _, cfg in pairs(switch.possibleConfigurations) do
    if cfg.id == configId then
      print("Setting active config id: " .. configId)
      rs.setOutput(switch.rsSide, cfg.rsOutput)
      ws.send(textutils.serializeJSON({
        cId=switch.id,
        type="SWITCH_UPDATE",
        activeConfigId=configId
      }))
      return true
    end
  end
  return false
end

local function trainOverhead(sb)
  return rs.getInput(sb.rsSide)
end

local function wasTrainOverhead(sb)
  return sb.trainOverhead == true
end

local function getTrainData(sb)
  return sb.augment.consist()
end

-- Sends an update to the API regarding a segment boundary event. The event
-- should be either "ENTERING" if a train has just entered a segment, or
-- "ENTERED" if a train has just completely entered a segment and left its
-- previous segment.
local function sendSegmentBoundaryUpdate(sb, eventType)
  local dir = string.upper(sb.trainLastData.direction)
  local toSegmentId = nil
  for _, segment in pairs(sb.segments) do
    if string.upper(segment.direction) == dir then
      toSegmentId = segment.id
      break
    end
  end
  print("Sending sb update: " .. toSegmentId .. ", " .. eventType)
  ws.send(textutils.serializeJSON({
    cId = sb.id,
    type = "SEGMENT_BOUNDARY_UPDATE",
    toSegmentId = toSegmentId,
    eventType = eventType
  }))
end

local function handleRedstoneEvent()
  for _, component in pairs(config.components) do
    if component.type == "SEGMENT_BOUNDARY" then
      local sb = component
      if trainOverhead(sb) and not wasTrainOverhead(sb) then
        local data = getTrainData(sb)
        if data ~= nil then
          sb.trainOverhead = true
          sb.trainLastData = data
          sendSegmentBoundaryUpdate(sb, "ENTERING")
          sb.trainScanTimer = os.startTimer(config.trainScanInterval)
        end
      end
    end
  end
end

-- Handles timer events that happen when we periodically check if a train that
-- was overhead is still there.
local function handleTrainScanTimerEvent(timerId)
  for _, component in pairs(config.components) do
    if component.trainScanTimer == timerId then
      local sb = component
      if trainOverhead(sb) then -- train is still overhead.
        local data = getTrainData(sb)
        if data ~= nil then sb.trainLastData = data end
        -- Schedule another timer to check again later.
        sb.trainOverhead = true
        sb.trainScanTimer = os.startTimer(config.trainScanInterval)
      else -- The train has left the signal, so send an update.
        sendSegmentBoundaryUpdate(sb, "ENTERED")
        sb.trainOverhead = false
        sb.trainLastData = nil
        sb.trainScanTimer = nil
      end
    end
  end
end

-- Process status updates for segments that we receive from the API.
local function handleSegmentStatusUpdate(msg)
  local signal = getComponentById(msg.cId)
  if signal ~= nil and signal.segment.id == msg.segmentId then
    signal.segment.occupied = msg.occupied
    displaySignal(signal)
  end
end

local function handleSwitchUpdate(msg)
  local switch = getComponentById(msg.cId)
  if switch ~= nil then
    setSwitchActiveConfig(switch, msg.activeConfigId)
  end
end

local function handleWebsocketMessage(msg)
  local data = textutils.unserializeJSON(msg)
  if data.type == "SEGMENT_STATUS" then
    handleSegmentStatusUpdate(data)
  elseif data.type == "SWITCH_UPDATE" then
    handleSwitchUpdate(data)
  elseif data.type == "ERROR" then
    print("Rail Signal sent an error: " .. data.message)
  end
end

-- Augments the configuration data with extra information from the API, such
-- as rail system information and component names.
local function initApiData()
  local tokenData = fetchJson("/lt/" .. config.linkToken)
  if tokenData == nil then
    print("Error: Could not fetch device component data from the Rail Signal API.")
    return false
  end
  for _, component in pairs(tokenData.components) do
    local c = getComponentById(component.id)
    if c == nil then
      print("Error: This device's link token is linked to component with id " .. component.id .. " but no component with that id is configured.")
      return false
    else
      c.name = component.name
    end
  end
  config.rsName = tokenData.rsName
  config.rsId = tokenData.rsId
  return true
end

-- MAIN SCRIPT
term.clear()
print("Rail Signal Device Driver " .. VERSION .. " for CC:Tweaked computers")
print("  By Andrew Lalis <andrewlalisofficial@gmail.com>")
print("-------------------------------------------------")
if #args < 1 then
  print("Missing required config filename argument.")
  return
end
local configFilename = args[1]
config = loadConfig(configFilename)
if config == nil then
  print("Error: Could not load config from file.")
  return
end
print("Loaded config.")

if initApiData() then
  print("Data fetched from Rail Signal API successfully.")
  print("Initialized for rail system \"" .. config.rsName .. "\", ID: " .. config.rsId)
else
  print("Could not fetch data from Rail Signal API.")
end

ws = connectToWebsocket()

-- Initialize all components.
for _, c in pairs(config.components) do
  if c.type == "SIGNAL" then
    c.segment.monitor = peripheral.wrap(c.segment.monitorId)
    local m = c.segment.monitor
    m.setPaletteColor(colors.white, 0xFFFFFF)
    m.setPaletteColor(colors.red, 0xFF0000)
    m.setPaletteColor(colors.lime, 0x00FF00)
    m.setPaletteColor(colors.blue, 0x0000FF)
    displaySignal(c)
  elseif c.type == "SEGMENT_BOUNDARY" then
    c.augment = peripheral.wrap(c.augmentId)
  elseif c.type == "SWITCH" then
    local state = rs.getOutput(c.rsSide)
    local activeConfigId = nil
    for _, cfg in pairs(c.possibleConfigurations) do
      if cfg.rsOutput == state then
        activeConfigId = cfg.id
      end
    end
    ws.send(textutils.serializeJSON({cid=c.id, type="SWITCH_UPDATE", activeConfigId=activeConfigId}))
  end
end

while true do
  local eventData = {os.pullEvent()}
  local event = eventData[1]
  if event == "redstone" then
    handleRedstoneEvent()
  elseif event == "websocket_message" then
    handleWebsocketMessage(eventData[3])
  elseif event == "websocket_closed" then
    for _, component in pairs(config.components) do
      if component.type == "SIGNAL" then
        component.segment.occupied = nil
        displaySignal(component)
      end
    end
    print("Rail Signal websocket closed. Attempting to reconnect.")
    os.sleep(0.5)
    ws = connectToWebsocket()
  elseif event == "timer" then
    handleTrainScanTimerEvent(eventData[2])
  end
end
