import axios from "axios";
import {API_URL} from "./constants";
import {refreshSomeComponents} from "./components";

/**
 * Updates the connections to a path node.
 * @param {RailSystem} rs The rail system to which the node belongs.
 * @param {Object} node The node to update.
 * @returns {Promise}
 */
export function updateConnections(rs, node) {
    return new Promise((resolve, reject) => {
        axios.patch(
            `${API_URL}/rs/${rs.id}/c/${node.id}/connectedNodes`,
            node
        )
            .then(response => {
                node.connectedNodes = response.data.connectedNodes;
                resolve();
            })
            .catch(reject);
    });
}

/**
 * Adds a connection to a path node.
 * @param {RailSystem} rs
 * @param {Object} node
 * @param {Object} other
 * @returns {Promise}
 */
export function addConnection(rs, node, other) {
    node.connectedNodes.push(other);
    return updateConnections(rs, node);
}

/**
 * Removes a connection from a path node.
 * @param {RailSystem} rs
 * @param {Object} node
 * @param {Object} other
 * @returns {Promise}
 */
export function removeConnection(rs, node, other) {
    const idx = node.connectedNodes.findIndex(n => n.id === other.id);
    return new Promise((resolve, reject) => {
        if (idx > -1) {
            node.connectedNodes.splice(idx, 1);
            updateConnections(rs, node)
                .then(() => {
                    const nodes = [];
                    nodes.push(...node.connectedNodes);
                    nodes.push(other);
                    refreshSomeComponents(rs, nodes)
                        .then(resolve)
                        .catch(reject);
                })
                .catch(reject);
        } else {
            resolve();
        }
    });

}