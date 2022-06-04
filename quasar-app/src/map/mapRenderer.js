/*
This is the main script which organizes the drawing of the rail system map.
 */

import { drawMap } from "./drawing";
import { camResetView, camScale, camTransformMapToWorld } from "./mapCamera";
import { initListeners, LAST_MOUSE_POINT } from "src/map/mapEventListener";

/**
 * The div containing the canvas element.
 * @type {Element | null}
 */
let mapContainerDiv = null;

/**
 * The canvas element.
 * @type {HTMLCanvasElement | null}
 */
export let MAP_CANVAS = null;

/**
 * The map's 2D rendering context.
 * @type {CanvasRenderingContext2D | null}
 */
export let MAP_CTX = null;

/**
 * The rail system that we're currently rendering.
 * @type {RailSystem | null}
 */
export let MAP_RAIL_SYSTEM = null;

/**
 * The set of components that are currently hovered over.
 * @type {Object[]}
 */
export const MAP_COMPONENTS_HOVERED = [];

export function initMap(rs) {
  MAP_RAIL_SYSTEM = rs;
  console.log("Initializing map for rail system: " + rs.name);
  camResetView();
  MAP_CANVAS = document.getElementById("railSystemMapCanvas");
  mapContainerDiv = document.getElementById("railSystemMapCanvasContainer");
  MAP_CTX = MAP_CANVAS?.getContext("2d");

  initListeners();

  // Do an initial draw.
  draw();
}

export function draw() {
  if (!(MAP_CANVAS && MAP_RAIL_SYSTEM && MAP_RAIL_SYSTEM.components)) {
    console.warn("Attempted to draw map without canvas or railSystem.");
    return;
  }
  if (MAP_CANVAS.width !== mapContainerDiv.clientWidth) {
    MAP_CANVAS.width = mapContainerDiv.clientWidth;
  }
  if (MAP_CANVAS.height !== mapContainerDiv.clientHeight) {
    MAP_CANVAS.height = mapContainerDiv.clientHeight - 6;
  }
  const width = MAP_CANVAS.width;
  const height = MAP_CANVAS.height;
  MAP_CTX.resetTransform();
  MAP_CTX.fillStyle = `rgb(240, 240, 240)`;
  MAP_CTX.fillRect(0, 0, width, height);

  drawMap();
  drawDebugInfo();
}

function drawDebugInfo() {
  MAP_CTX.resetTransform();
  MAP_CTX.fillStyle = "black";
  MAP_CTX.strokeStyle = "black";
  MAP_CTX.font = "10px Sans-Serif";
  const lastWorldPoint = camTransformMapToWorld(LAST_MOUSE_POINT);
  const lines = [
    "Scale factor: " + camScale(),
    `(x = ${lastWorldPoint.x.toFixed(2)}, y = ${lastWorldPoint.y.toFixed(2)}, z = ${lastWorldPoint.z.toFixed(2)})`,
    `Components: ${MAP_RAIL_SYSTEM.components.length}`,
    `Hovered components: ${MAP_COMPONENTS_HOVERED.length}`
  ];
  for (let i = 0; i < MAP_COMPONENTS_HOVERED.length; i++) {
    lines.push("  " + MAP_COMPONENTS_HOVERED[i].name);
  }
  for (let i = 0; i < lines.length; i++) {
    MAP_CTX.fillText(lines[i], 10, 20 + (i * 15));
  }
}

export function isComponentHovered(component) {
  return MAP_COMPONENTS_HOVERED.some(c => c.id === component.id);
}

export function isComponentSelected(component) {
  return MAP_RAIL_SYSTEM.selectedComponents.some(c => c.id === component.id);
}
