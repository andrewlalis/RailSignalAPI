import axios from "axios";
import {API_URL} from "./constants";

/**
 * Fetches the set of segments for a rail system.
 * @param {Number} rsId
 * @returns {Promise<[Object]>}
 */
export function getSegments(rsId) {
    return new Promise((resolve, reject) => {
        axios.get(`${API_URL}/rs/${rsId}/s`)
            .then(response => resolve(response.data))
            .catch(error => reject(error));
    });
}

export function refreshSegments(rs) {
    return new Promise(resolve => {
        getSegments(rs.id)
            .then(segments => {
                rs.segments = segments;
                resolve();
            })
            .catch(error => console.error(error));
    });
}

export function createSegment(rs, name) {
    return new Promise((resolve, reject) => {
        axios.post(`${API_URL}/rs/${rs.id}/s`, {name: name})
            .then(() => {
                refreshSegments(rs)
                    .then(() => resolve())
                    .catch(error => reject(error));
            })
            .catch(error => reject(error));
    });
}

export function removeSegment(rs, segmentId) {
    return new Promise((resolve, reject) => {
        axios.delete(`${API_URL}/rs/${rs.id}/s/${segmentId}`)
            .then(() => {
                refreshSegments(rs)
                    .then(() => resolve())
                    .catch(error => reject(error));
            })
            .catch(error => reject(error));
    });
}
