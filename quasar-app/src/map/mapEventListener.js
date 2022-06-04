import { draw, MAP_COMPONENTS_HOVERED, MAP_CANVAS, MAP_RAIL_SYSTEM } from "src/map/mapRenderer";
import {
  camPanActive,
  camPanFinish,
  camPanMove,
  camPanNonzero,
  camPanStart, camResetView, camTransformWorldToMap,
  camZoomIn,
  camZoomOut
} from "src/map/mapCamera";
import { getMousePoint } from "src/map/canvasUtils";

const HOVER_RADIUS = 10;

export let LAST_MOUSE_POINT = null;

const componentSelectionListeners = new Map();

/**
 * Initializes all event listeners for the map controls.
 */
export function initListeners() {
  registerListener(MAP_CANVAS, "wheel", onMouseWheel);
  registerListener(MAP_CANVAS, "mousedown", onMouseDown);
  registerListener(MAP_CANVAS, "mouseup", onMouseUp);
  registerListener(MAP_CANVAS, "mousemove", onMouseMove);
  registerListener(window, "keydown", onKeyDown);
}

function registerListener(element, type, callback) {
  element.removeEventListener(type, callback);
  element.addEventListener(type, callback);
}

export function registerComponentSelectionListener(name, callback) {
  componentSelectionListeners.set(name, callback);
}

/**
 * Handles mouse scroll wheel. This is used to control camera zoom.
 * @param {WheelEvent} event
 */
function onMouseWheel(event) {
  if (!event.shiftKey) return;
  const s = event.deltaY;
  if (s < 0) {
    camZoomIn();
  } else if (s > 0) {
    camZoomOut();
  }
  draw();
  event.stopPropagation();
  return false;
}

/**
 * Handles mouse down clicks. We only use this to start tracking panning.
 * @param {MouseEvent} event
 */
function onMouseDown(event) {
  const p = getMousePoint(event);
  if (event.shiftKey) {
    camPanStart({ x: p.x, y: p.y });
  }
}

/**
 * Handles mouse up events. This means we do the following:
 * - Finish any camera panning, if it was active.
 * - Select any components that the mouse is close enough to.
 * - If no components were selected, clear the list of selected components.
 * @param {MouseEvent} event
 */
function onMouseUp(event) {
  const finishingDrag = camPanNonzero();
  camPanFinish();
  if (MAP_COMPONENTS_HOVERED.length > 0) {
    if (!event.shiftKey) {// If the user isn't holding SHIFT, clear the set of selected components first.
      MAP_RAIL_SYSTEM.selectedComponents.length = 0;
    }
    // If the user is clicking on a component that's already selected, deselect it.
    for (let i = 0; i < MAP_COMPONENTS_HOVERED.length; i++) {
      const hoveredComponent = MAP_COMPONENTS_HOVERED[i];
      const idx = MAP_RAIL_SYSTEM.selectedComponents.findIndex(c => c.id === hoveredComponent.id);
      if (idx > -1) {
        MAP_RAIL_SYSTEM.selectedComponents.splice(idx, 1);
      } else {
        MAP_RAIL_SYSTEM.selectedComponents.push(hoveredComponent);
      }
    }
    componentSelectionListeners.forEach(callback => callback(MAP_RAIL_SYSTEM.selectedComponents));
  } else if (!finishingDrag) {
    MAP_RAIL_SYSTEM.selectedComponents.length = 0;
  }
}

/**
 * Handle when the mouse moves.
 * @param {MouseEvent} event
 */
function onMouseMove(event) {
  const p = getMousePoint(event);
  LAST_MOUSE_POINT = p;
  if (camPanActive()) {
    if (event.shiftKey) {
      camPanMove({ x: p.x, y: p.y });
    } else {
      camPanFinish();
    }
  } else {
    MAP_COMPONENTS_HOVERED.length = 0;
    // Populate with list of hovered elements.
    for (let i = 0; i < MAP_RAIL_SYSTEM.components.length; i++) {
      const c = MAP_RAIL_SYSTEM.components[i];
      const componentPoint = new DOMPoint(c.position.x, c.position.z, 0, 1);
      const mapComponentPoint = camTransformWorldToMap(componentPoint);
      const dist2 = (p.x - mapComponentPoint.x) * (p.x - mapComponentPoint.x) + (p.y - mapComponentPoint.y) * (p.y - mapComponentPoint.y);
      if (dist2 < HOVER_RADIUS * HOVER_RADIUS) {
        MAP_COMPONENTS_HOVERED.push(c);
      }
    }
  }
  draw();
}

/**
 * @param {KeyboardEvent} event
 */
function onKeyDown(event) {
  if (event.ctrlKey && event.code === "Space") {
    camResetView();
  }
}
