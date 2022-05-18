import {WS_URL} from "./constants";

export function establishWebsocketConnection(rs) {
    if (rs.websocket) {
        rs.websocket.close();
    }
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

export function closeWebsocketConnection(rs) {
    if (rs.websocket) {
        rs.websocket.close();
        rs.websocket = null;
    }
}
