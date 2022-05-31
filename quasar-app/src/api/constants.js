/*
In development mode, we should use localhost:8080 as that's where the API is set
to run. In production mode, this app is deployed under the same host as the API,
so we can simply use `location.origin` and `location.host` to define our API and
WS urls.
 */
export const API_URL = process.env.DEV
  ? "http://localhost:8080/api"
  : location.origin + "/api";
export const WS_URL = process.env.DEV
  ? "ws://localhost:8080/api/ws/app"
  : (location.protocol === "https:" ? "wss://" : "ws://") + location.host + "/api/ws/app";
