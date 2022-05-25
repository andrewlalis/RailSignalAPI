import {API_URL} from "src/api/constants";
import axios from "axios";

export function refreshSettings(rs) {
  return new Promise((resolve, reject) => {
    axios.get(`${API_URL}/rs/${rs.id}/settings`)
      .then(response => {
        rs.settings = response.data;
        resolve();
      })
      .catch(reject);
  });
}

export function updateSettings(rs, newSettings) {
  return new Promise((resolve, reject) => {
    axios.post(`${API_URL}/rs/${rs.id}/settings`, newSettings)
      .then(() => {
        refreshSettings(rs).then(resolve).catch(reject);
      })
      .catch(reject);
  });
}
