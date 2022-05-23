import {API_URL} from "./constants";
import axios from "axios";

/**
 * A token that's used by components to provide real-time up and down links.
 */
export class LinkToken {
    constructor(data) {
        this.id = data.id;
        this.label = data.label;
        this.components = data.components;
    }
}

/**
 * Gets the list of link tokens in a rail system.
 * @param {RailSystem} rs
 * @return {Promise<LinkToken[]>}
 */
export function getLinkTokens(rs) {
    return new Promise((resolve, reject) => {
        axios.get(`${API_URL}/rs/${rs.id}/lt`)
            .then(response => {
                resolve(response.data.map(obj => new LinkToken(obj)));
            })
            .catch(reject);
    });
}

/**
 * Creates a new link token.
 * @param {RailSystem} rs
 * @param {LinkToken} data
 * @return {Promise<string>} A promise that resolves to the token that was created.
 */
export function createLinkToken(rs, data) {
    return new Promise((resolve, reject) => {
        axios.post(`${API_URL}/rs/${rs.id}/lt`, data)
            .then(response => {
                resolve(response.data.token);
            })
            .catch(reject);
    });
}

/**
 * Deletes a link token.
 * @param {RailSystem} rs
 * @param {Number} tokenId
 */
export function deleteToken(rs, tokenId) {
    return new Promise((resolve, reject) => {
        axios.delete(`${API_URL}/rs/${rs.id}/lt/${tokenId}`)
            .then(resolve)
            .catch(reject);
    });
}
