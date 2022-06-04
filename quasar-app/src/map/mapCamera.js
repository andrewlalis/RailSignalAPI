import { MAP_CANVAS } from "src/map/mapRenderer";

const SCALE_VALUES = [0.01, 0.1, 0.25, 0.5, 1.0, 1.25, 1.5, 2.0, 3.0, 4.0, 6.0, 8.0, 10.0, 12.0, 16.0, 20.0, 30.0, 45.0, 60.0, 80.0, 100.0];
const SCALE_INDEX_NORMAL = 7;

let scaleIndex = SCALE_INDEX_NORMAL;
let translation = {x: 0, y: 0};
let panOrigin = null;
let panTranslation = null;
let lastPanPoint = null;

/**
 * Resets the camera view to the default values.
 */
export function camResetView() {
  scaleIndex = SCALE_INDEX_NORMAL;
  translation.x = 0;
  translation.y = 0;
  panOrigin = null;
  panTranslation = null;
  lastPanPoint = null;
}

/**
 * Zooms in the camera, if possible.
 */
export function camZoomIn() {
  if (scaleIndex < SCALE_VALUES.length - 1) {
    scaleIndex++;
  }
}

/**
 * Zooms out the camera, if possible.
 */
export function camZoomOut() {
  if (scaleIndex > 0) {
    scaleIndex--;
  }
}

/**
 * Gets the current zoom scale of the camera.
 * @returns {number}
 */
export function camScale() {
  return SCALE_VALUES[scaleIndex];
}

/**
 * Gets the camera transform that's used to map world coordinates to the canvas.
 * @return {DOMMatrix}
 */
export function camGetTransform() {
  const tx = new DOMMatrix();
  const canvasRect = MAP_CANVAS.getBoundingClientRect();
  tx.translateSelf(canvasRect.width / 2, canvasRect.height / 2, 0);
  const scale = SCALE_VALUES[scaleIndex];
  tx.scaleSelf(scale, scale, scale);
  tx.translateSelf(translation.x, translation.y, 0);
  if (panOrigin && panTranslation) {
    tx.translateSelf(panTranslation.x, panTranslation.y, 0);
  }
  return tx;
}

export function camPanStart(point) {
  panOrigin = point;
  panTranslation = {x: 0, y: 0};
}

export function camPanActive() {
  return panOrigin !== null;
}

export function camPanNonzero() {
  return panTranslation !== null && (panTranslation.x !== 0 || panTranslation.y !== 0);
}

export function camPanMove(point) {
  if (panOrigin) {
    lastPanPoint = point;
    const scale = SCALE_VALUES[scaleIndex];
    const dx = point.x - panOrigin.x;
    const dy = point.y - panOrigin.y;
    panTranslation = {x: dx / scale, y: dy / scale};
  }
}

export function camPanFinish() {
  if (panTranslation) {
    translation.x += panTranslation.x;
    translation.y += panTranslation.y;
  }
  panOrigin = null;
  panTranslation = null;
}

/**
 * Maps a point on the map coordinates to world coordinates.
 * @param {DOMPoint} point
 * @returns {DOMPoint}
 */
export function camTransformMapToWorld(point) {
  return camGetTransform().invertSelf().transformPoint(point);
}

/**
 * Maps a point in the world to map coordinates.
 * @param {DOMPoint} point
 * @returns {DOMPoint}
 */
export function camTransformWorldToMap(point) {
  return camGetTransform().transformPoint(point);
}
