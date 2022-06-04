import { MAP_CANVAS } from "src/map/mapRenderer";

export function roundedRect(ctx, x, y, w, h, r) {
    if (w < 2 * r) r = w / 2;
    if (h < 2 * r) r = h / 2;
    ctx.beginPath();
    ctx.moveTo(x+r, y);
    ctx.arcTo(x+w, y,   x+w, y+h, r);
    ctx.arcTo(x+w, y+h, x,   y+h, r);
    ctx.arcTo(x,   y+h, x,   y,   r);
    ctx.arcTo(x,   y,   x+w, y,   r);
    ctx.closePath();
}

export function circle(ctx, x, y, r) {
    ctx.beginPath();
    ctx.arc(x, y, r, 0, Math.PI * 2);
}

export function mulberry32(a) {
  return function() {
    let t = a += 0x6D2B79F5;
    t = Math.imul(t ^ t >>> 15, t | 1);
    t ^= t + Math.imul(t ^ t >>> 7, t | 61);
    return ((t ^ t >>> 14) >>> 0) / 4294967296;
  }
}

export function sortPoints(points) {
  points = points.splice(0);
  const p0 = {};
  p0.y = Math.min.apply(null, points.map(p=>p.y));
  p0.x = Math.max.apply(null, points.filter(p=>p.y === p0.y).map(p=>p.x));
  points.sort((a,b)=>angleCompare(p0, a, b));
  return points;
}

function angleCompare(p0, a, b) {
  const  left = isLeft(p0, a, b);
  if (left === 0) return distCompare(p0, a, b);
  return left;
}

function isLeft(p0, a, b) {
  return (a.x-p0.x)*(b.y-p0.y) - (b.x-p0.x)*(a.y-p0.y);
}

function distCompare(p0, a, b) {
  const distA = (p0.x-a.x)*(p0.x-a.x) + (p0.y-a.y)*(p0.y-a.y);
  const distB = (p0.x-b.x)*(p0.x-b.x) + (p0.y-b.y)*(p0.y-b.y);
  return distA - distB;
}

/**
 * Gets the point at which the user clicked on the map.
 * @param {MouseEvent} event
 * @returns {DOMPoint}
 */
export function getMousePoint(event) {
  const rect = MAP_CANVAS.getBoundingClientRect();
  const x = event.clientX - rect.left;
  const y = event.clientY - rect.top;
  return new DOMPoint(x, y, 0, 1);
}
