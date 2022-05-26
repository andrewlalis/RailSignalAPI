# Component Drivers
This document describes the general methodology for writing component drivers that operate within your rail system to connect it to the online system's up- and down-link services.

The following types of components are supported by Rail Signal:
- `SIGNAL`
- `SEGMENT_BOUNDARY`
- `SWITCH`

The following information is generally required for any driver to be able to connect to the system and operate nominally:
- A valid link token
- The base URL of the system's API. Usually `http://localhost:8080` or whatever you've configured it to be.
- Live connection information. This differs depending on what type of communication your device supports.
    - For devices with **websocket** support, you will need the base URL of the websocket. Usually `ws://localhost:8080` or whatever you've configured your server to use.
    - For devices with **TCP socket** support, you will need the hostname and port of the system's server socket. By default, this is `localhost:8081`.

A device is not limited to a single component, but will act as a relay for all components linked to the device's link token. Generally, there is no limit to the number of components that a single device can manage, but more components will lead to more load on the device.

## Live Communication
The main purpose of component drivers is to relay real-time messages between actual component devices, and their representations in the online rail system. While multiple types of communication are available, in general, all messages are sent as JSON UTF-8 encoded strings. Devices must present their link token when they initiate the connection.

If the link token is valid, the connection will be initiated, and the device will immediately receive a `COMPONENT_DATA` message for each component that the token is linked to.

### Websocket
Websocket connections should be made to `{BASE_WS_URL}/api/ws/component?token={token}`, where `{BASE_WS_URL}` is the base websocket URL, such as `ws://localhost:8080`, and `{token}` is the device's link token.

- If the link token is missing or improperly formatted, a 400 Bad Request response is given.
- If the link token is not correct or not active or otherwise set to reject connections, a 401 Unauthorized response is given.

### TCP Socket
TCP socket connections should be made to the server's TCP socket address, which by default is `localhost:8081`. The device should immediately send 2 bytes indicating the length of its token string, followed by the token string's bytes. The client should expect to receive in response a *connect message*:
```json
{
  "valid": true,
  "message": "Connection established."
}
```

- If the token is invalid, a `"valid": false` is returned and the server closes the socket.

Note: All messages sent via TCP are sent as JSON messages with a 2-byte length header.

## Components
Each device should be designed to handle multiple independent components concurrently. The device may receive messages at any time pertaining to any of the components, and the device may send messages at any time, pertaining to any of the components. Messages are only sent regarding a single component.

Every component message should contain at least the following two properties:
```json
{
  "cId": 123,
  "type": "COMPONENT_DATA",
  ...
}
```
`cId` is the id of the component that this message is about. `type` is the type of message. This defines what additional structure to expect.

All components may receive `COMPONENT_DATA` messages. For example, the following could be a message regarding a signal:
```json
{
  "cId": 123,
  "type": "COMPONENT_DATA",
  "data": {
    "id": 123,
    "position": {"x": 0, "y": 0, "z": 0},
    "name": "my-component",
    "type": "SIGNAL",
    "online": true,
    "segment": {
      "id": 4,
      "name": "my-segment",
      "occupied": false
    }
  }
}
```

The following sections will provide more detail about the other types of messages that can be sent and received by the different components.

### Signal
Signals display the status of a connected segment, and as such can only receive data. They will receive `SEGMENT_STATUS` messages:
```json
{
  "cId": 123,
  "type": "SEGMENT_STATUS",
  "sId": 4,
  "occupied": true
}
```
`sId` is the id of the segment that was updated. `occupied` contains the current status of the segment.

### Segment Boundary
Segment boundaries send updates as trains pass them, in order to provide information to the system about the state of connected segments.
```json
{
  "cId": 123,
  "type": "SEGMENT_BOUNDARY_UPDATE",
  "toSegmentId": 3,
  "eventType": "ENTERING"
}
```
`toSegmentId` is the id of the segment a train is moving towards. `eventType` is the type of boundary event. This can either be `ENTERING` if a train has just begun entering the segment, or `ENTERED` if a train has just left the boundary and completely entered the segment.

### Switch
Switches can send information about their status, if it's been updated, and they can also receive messages that direct them to change their status.

```json
{
  "cId": 123,
  "type": "SWITCH_UPDATE",
  "activeConfigId": 497238
}
```
`activeConfigId` is the id of the switch configuration that's active. This message can be sent by either the system or the switch.
