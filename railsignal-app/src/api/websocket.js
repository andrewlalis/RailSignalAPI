import {WS_URL} from "./constants";

/**
 * Establishes a websocket connection to the given rail system.
 * @param {RailSystem} rs
 */
export function establishWebsocketConnection(rs) {
    closeWebsocketConnection(rs);
    rs.websocket = new WebSocket(`${WS_URL}/${rs.id}`);
    rs.websocket.onopen = () => {
        console.log("Opened websocket connection to rail system " + rs.id);
    };
    rs.websocket.onclose = event => {
        if (event.code !== 1000) {
            console.warn("Lost websocket connection. Attempting to reestablish.");
            setTimeout(() => {
                establishWebsocketConnection(rs);
            }, 3000);
        }
        console.log("Closed websocket connection to rail system " + rs.id);
    };
    rs.websocket.onmessage = msg => {
        console.log(msg);
    };
    rs.websocket.onerror = error => {
        console.log(error);
    };
}

/**
 * Closes the websocket connection to a rail system, if possible.
 * @param {RailSystem} rs
 */
export function closeWebsocketConnection(rs) {
    if (rs.websocket) {
        rs.websocket.close();
        rs.websocket = null;
    }
}
