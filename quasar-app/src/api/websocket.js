import { WS_URL } from "./constants";

const WS_RECONNECT_TIMEOUT = 3000;

/**
 * Establishes a websocket connection to the given rail system.
 * @param {RailSystem} rs
 * @return {Promise} A promise that resolves when a connection is established.
 */
export function establishWebsocketConnection(rs) {
  return new Promise(resolve => {
    rs.websocket = new WebSocket(`${WS_URL}/${rs.id}`);
    rs.websocket.onopen = resolve;
    rs.websocket.onclose = event => {
      if (event.code === 1000) {
        console.log(`Closed websocket connection to rail system "${rs.name}" (${rs.id})`);
      } else {
        console.warn(`Unexpectedly lost websocket connection to rail system "${rs.name}" (${rs.id}). Attempting to reestablish in ${WS_RECONNECT_TIMEOUT} ms.`);
        setTimeout(() => {
          establishWebsocketConnection(rs)
            .then(() => console.log("Successfully reestablished connection."));
        }, WS_RECONNECT_TIMEOUT);
      }
    };
    rs.websocket.onmessage = msg => {
      const data = JSON.parse(msg.data);
      console.log(data);
      if (data.type === "COMPONENT_DATA") {
        const id = data.cId;
        const idx = rs.components.findIndex(c => c.id === id);
        if (idx > -1) {
          rs.components[idx] = data.data;
        }
      }
    };
    rs.websocket.onerror = error => {
      console.log(error);
    };
  });
}

/**
 * Closes the websocket connection to a rail system, if possible.
 * @param {RailSystem} rs
 * @return {Promise} A promise that resolves when the connection is closed.
 */
export function closeWebsocketConnection(rs) {
  return new Promise(resolve => {
    if (rs.websocket) {
      rs.websocket.onclose = resolve;
      rs.websocket.close();
    } else {
      resolve();
    }
  });

}
