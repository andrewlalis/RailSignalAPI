/*
This component is responsible for the rendering of a RailSystem in a 2d map
view.
 */

import { drawMap } from "./drawing";

const SCALE_VALUES = [0.01, 0.1, 0.25, 0.5, 1.0, 1.25, 1.5, 2.0, 3.0, 4.0, 6.0, 8.0, 10.0, 12.0, 16.0, 20.0, 30.0, 45.0, 60.0, 80.0, 100.0];
const SCALE_INDEX_NORMAL = 7;
const HOVER_RADIUS = 10;

let mapContainerDiv = null;
let mapCanvas = null;
let railSystem = null;

let mapScaleIndex = SCALE_INDEX_NORMAL;
let mapTranslation = {x: 0, y: 0};
let mapDragOrigin = null;
let mapDragTranslation = null;
let lastMousePoint = new DOMPoint(0, 0, 0, 0);
const hoveredElements = [];

export function initMap(rs) {
    railSystem = rs;
    console.log("Initializing map for rail system: " + rs.name);
    hoveredElements.length = 0;
    mapCanvas = document.getElementById("railSystemMapCanvas");
    mapContainerDiv = document.getElementById("railSystemMapCanvasContainer");
    mapCanvas.removeEventListener("wheel", onMouseWheel);
    mapCanvas.addEventListener("wheel", onMouseWheel);
    mapCanvas.removeEventListener("mousedown", onMouseDown);
    mapCanvas.addEventListener("mousedown", onMouseDown);
    mapCanvas.removeEventListener("mouseup", onMouseUp);
    mapCanvas.addEventListener("mouseup", onMouseUp);
    mapCanvas.removeEventListener("mousemove", onMouseMove);
    mapCanvas.addEventListener("mousemove", onMouseMove);

    // Do an initial draw.
    draw();
}

export function draw() {
    if (!(mapCanvas && railSystem && railSystem.components)) {
        console.warn("Attempted to draw map without canvas or railSystem.");
        return;
    }
    const ctx = mapCanvas.getContext("2d");
    if (mapCanvas.width !== mapContainerDiv.clientWidth) {
        mapCanvas.width = mapContainerDiv.clientWidth;
    }
    if (mapCanvas.height !== mapContainerDiv.clientHeight) {
        mapCanvas.height = mapContainerDiv.clientHeight - 6;
    }
    const width = mapCanvas.width;
    const height = mapCanvas.height;
    ctx.resetTransform();
    ctx.fillStyle = `rgb(240, 240, 240)`;
    ctx.fillRect(0, 0, width, height);

    drawMap(ctx, railSystem);
    drawDebugInfo(ctx);
}

function drawDebugInfo(ctx) {
  ctx.resetTransform();
  ctx.fillStyle = "black";
  ctx.strokeStyle = "black";
  ctx.font = "10px Sans-Serif";
  const lastWorldPoint = mapPointToWorld(lastMousePoint);
  const lines = [
    "Scale factor: " + getScaleFactor(),
    `(x = ${lastWorldPoint.x.toFixed(2)}, y = ${lastWorldPoint.y.toFixed(2)}, z = ${lastWorldPoint.z.toFixed(2)})`,
    `Components: ${railSystem.components.length}`,
    `Hovered elements: ${hoveredElements.length}`
  ]
  for (let i = 0; i < lines.length; i++) {
    ctx.fillText(lines[i], 10, 20 + (i * 15));
  }
}

export function getScaleFactor() {
    return SCALE_VALUES[mapScaleIndex];
}

/**
 * Gets a matrix that transforms world coordinates to canvas.
 * @returns {DOMMatrix}
 */
export function getWorldTransform() {
    const canvasRect = mapCanvas.getBoundingClientRect();
    const scale = getScaleFactor();
    const tx = new DOMMatrix();
    tx.translateSelf(canvasRect.width / 2, canvasRect.height / 2, 0);
    tx.scaleSelf(scale, scale, scale);
    tx.translateSelf(mapTranslation.x, mapTranslation.y, 0);
    if (mapDragOrigin !== null && mapDragTranslation !== null) {
        tx.translateSelf(mapDragTranslation.x, mapDragTranslation.y, 0);
    }
    return tx;
}

export function isComponentHovered(component) {
  return hoveredElements.some(c => c.id === component.id);
}

export function isComponentSelected(component) {
  return railSystem.selectedComponents.some(c => c.id === component.id);
}

/**
 * Maps a point on the map coordinates to world coordinates.
 * @param {DOMPoint} p
 * @returns {DOMPoint}
 */
export function mapPointToWorld(p) {
    return getWorldTransform().invertSelf().transformPoint(p);
}

/**
 * Maps a point in the world to map coordinates.
 * @param {DOMPoint} p
 * @returns {DOMPoint}
 */
export function worldPointToMap(p) {
    return getWorldTransform().transformPoint(p);
}

/*
EVENT HANDLING
*/

/**
 * @param {WheelEvent} event
 */
function onMouseWheel(event) {
    const s = event.deltaY;
    if (s > 0) {
        mapScaleIndex = Math.max(0, mapScaleIndex - 1);
    } else if (s < 0) {
        mapScaleIndex = Math.min(SCALE_VALUES.length - 1, mapScaleIndex + 1);
    }
    draw();
    event.stopPropagation();
    return false;
}

/**
 * @param {MouseEvent} event
 */
function onMouseDown(event) {
    const p = getMousePoint(event);
    mapDragOrigin = {x: p.x, y: p.y};
}

function onMouseUp() {
    let finishedDrag = false;
    if (mapDragTranslation !== null) {
        mapTranslation.x += mapDragTranslation.x;
        mapTranslation.y += mapDragTranslation.y;
        finishedDrag = true;
    }
    if (hoveredElements.length > 0) {
      railSystem.selectedComponents.length = 0;
      railSystem.selectedComponents.push(...hoveredElements);
    } else if (!finishedDrag) {
      railSystem.selectedComponents.length = 0;
    }
    mapDragOrigin = null;
    mapDragTranslation = null;
}

/**
 * @param {MouseEvent} event
 */
function onMouseMove(event) {
    const p = getMousePoint(event);
    lastMousePoint = p;
    if (mapDragOrigin !== null) {
        const scale = getScaleFactor();
        const dx = p.x - mapDragOrigin.x;
        const dy = p.y - mapDragOrigin.y;
        mapDragTranslation = {x: dx / scale, y: dy / scale};
    } else {
        hoveredElements.length = 0;
        // Populate with list of hovered elements.
        for (let i = 0; i < railSystem.components.length; i++) {
            const c = railSystem.components[i];
            const componentPoint = new DOMPoint(c.position.x, c.position.z, 0, 1);
            const mapComponentPoint = worldPointToMap(componentPoint);
            const dist2 = (p.x - mapComponentPoint.x) * (p.x - mapComponentPoint.x) + (p.y - mapComponentPoint.y) * (p.y - mapComponentPoint.y);
            if (dist2 < HOVER_RADIUS * HOVER_RADIUS) {
                hoveredElements.push(c);
            }
        }
    }
    draw();
}

/**
 * Gets the point at which the user clicked on the map.
 * @param {MouseEvent} event
 * @returns {DOMPoint}
 */
function getMousePoint(event) {
    const rect = mapCanvas.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    return new DOMPoint(x, y, 0, 1);
}
