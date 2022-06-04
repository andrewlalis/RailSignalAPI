/*
Helper functions to actually perform rendering of different components.
*/

import { isComponentHovered, isComponentSelected, MAP_CTX, MAP_RAIL_SYSTEM } from "./mapRenderer";
import { circle, roundedRect, sortPoints } from "./canvasUtils";
import randomColor from "randomcolor";
import { camGetTransform, camScale } from "src/map/mapCamera";

export function drawMap() {
  const worldTx = camGetTransform();
  MAP_CTX.setTransform(worldTx);
  drawSegments();
  drawNodeConnections(worldTx);
  drawComponents(worldTx);
}

function drawSegments() {
  const segmentPoints = new Map();
  // Gather for each segment a set of points representing its bounds.
  MAP_RAIL_SYSTEM.segments.forEach(segment => segmentPoints.set(segment.id, []));
  for (let i = 0; i < MAP_RAIL_SYSTEM.components.length; i++) {
    const c = MAP_RAIL_SYSTEM.components[i];
    if (c.type === "SEGMENT_BOUNDARY") {
      for (let j = 0; j < c.segments.length; j++) {
        segmentPoints.get(c.segments[j].id).push({ x: c.position.x, y: c.position.z });
      }
    }
  }
  // Sort the points to make regular convex polygons.
  for (let i = 0; i < MAP_RAIL_SYSTEM.segments.length; i++) {
    const unsortedPoints = segmentPoints.get(MAP_RAIL_SYSTEM.segments[i].id);
    segmentPoints.set(MAP_RAIL_SYSTEM.segments[i].id, sortPoints(unsortedPoints));
  }

  for (let i = 0; i < MAP_RAIL_SYSTEM.segments.length; i++) {
    const segment = MAP_RAIL_SYSTEM.segments[i];
    const color = randomColor({ luminosity: "light", format: "rgb", seed: segment.id });
    MAP_CTX.fillStyle = color;
    MAP_CTX.strokeStyle = color;
    MAP_CTX.lineWidth = 5;
    MAP_CTX.lineCap = "round";
    MAP_CTX.lineJoin = "round";
    MAP_CTX.font = "3px Sans-Serif";

    const points = segmentPoints.get(segment.id);
    if (points.length === 0) continue;
    const avgPoint = { x: points[0].x, y: points[0].y };
    if (points.length === 1) {
      circle(MAP_CTX, points[0].x, points[0].y, 5);
      MAP_CTX.fill();
    } else {
      MAP_CTX.beginPath();
      MAP_CTX.moveTo(points[0].x, points[0].y);
      for (let j = 1; j < points.length; j++) {
        MAP_CTX.lineTo(points[j].x, points[j].y);
        avgPoint.x += points[j].x;
        avgPoint.y += points[j].y;
      }
      avgPoint.x /= points.length;
      avgPoint.y /= points.length;
      MAP_CTX.lineTo(points[0].x, points[0].y);
      MAP_CTX.fill();
      MAP_CTX.stroke();
    }

    // Draw the segment name.
    MAP_CTX.fillStyle = randomColor({ luminosity: "dark", format: "rgb", seed: segment.id });
    MAP_CTX.fillText(segment.name, avgPoint.x, avgPoint.y);
  }
}

function drawNodeConnections(worldTx) {
  for (let i = 0; i < MAP_RAIL_SYSTEM.components.length; i++) {
    const c = MAP_RAIL_SYSTEM.components[i];
    if (c.connectedNodes !== undefined && c.connectedNodes !== null) {
      drawConnectedNodes(worldTx, c);
    }
  }
}

function drawComponents(worldTx) {
  // Draw switch configurations first
  for (let i = 0; i < MAP_RAIL_SYSTEM.components.length; i++) {
    const c = MAP_RAIL_SYSTEM.components[i];
    if (c.type === "SWITCH") {
      drawSwitchConfigurations(worldTx, c);
    }
  }

  for (let i = 0; i < MAP_RAIL_SYSTEM.components.length; i++) {
    drawComponent(worldTx, MAP_RAIL_SYSTEM.components[i]);
  }
}

function drawComponent(worldTx, component) {
  const componentTransform = DOMMatrix.fromMatrix(worldTx);
  componentTransform.translateSelf(component.position.x, component.position.z, 0);
  const s = camScale();
  componentTransform.scaleSelf(1 / s, 1 / s, 1 / s);
  componentTransform.scaleSelf(20, 20, 20);
  MAP_CTX.setTransform(componentTransform);

  if (component.type === "SIGNAL") {
    drawSignal(component);
  } else if (component.type === "SEGMENT_BOUNDARY") {
    drawSegmentBoundary();
  } else if (component.type === "SWITCH") {
    drawSwitch();
  } else if (component.type === "LABEL") {
    drawLabel(component);
  }

  MAP_CTX.setTransform(componentTransform.translate(0.75, -0.75));
  if (component.online !== undefined && component.online !== null) {
    drawOnlineIndicator(component);
  }

  MAP_CTX.setTransform(componentTransform);
  // Draw hovered status.
  if (isComponentHovered(component) || isComponentSelected(component)) {
    MAP_CTX.fillStyle = `rgba(255, 255, 0, 0.5)`;
    circle(MAP_CTX, 0, 0, 0.75);
    MAP_CTX.fill();
  }
}

function drawSignal(signal) {
  roundedRect(MAP_CTX, -0.3, -0.5, 0.6, 1, 0.25);
  MAP_CTX.fillStyle = "black";
  MAP_CTX.fill();
  if (signal.segment) {
    if (signal.segment.occupied === true) {
      MAP_CTX.fillStyle = `rgb(255, 0, 0)`;
    } else if (signal.segment.occupied === false) {
      MAP_CTX.fillStyle = `rgb(0, 255, 0)`;
    } else {
      MAP_CTX.fillStyle = `rgb(255, 255, 0)`;
    }
  } else {
    MAP_CTX.fillStyle = `rgb(0, 0, 255)`;
  }
  circle(MAP_CTX, 0, -0.2, 0.15);
  MAP_CTX.fill();
}

function drawSegmentBoundary() {
  MAP_CTX.fillStyle = `rgb(150, 58, 224)`;
  MAP_CTX.beginPath();
  MAP_CTX.moveTo(0, -0.5);
  MAP_CTX.lineTo(-0.5, 0);
  MAP_CTX.lineTo(0, 0.5);
  MAP_CTX.lineTo(0.5, 0);
  MAP_CTX.lineTo(0, -0.5);
  MAP_CTX.fill();
}

function drawSwitchConfigurations(worldTx, sw) {
  const tx = DOMMatrix.fromMatrix(worldTx);
  tx.translateSelf(sw.position.x, sw.position.z, 0);
  const s = camScale();
  tx.scaleSelf(1 / s, 1 / s, 1 / s);
  tx.scaleSelf(20, 20, 20);
  MAP_CTX.setTransform(tx);

  for (let i = 0; i < sw.possibleConfigurations.length; i++) {
    const config = sw.possibleConfigurations[i];
    MAP_CTX.strokeStyle = randomColor({
      seed: config.id,
      format: "rgb",
      luminosity: "bright"
    });
    if (sw.activeConfiguration !== null && sw.activeConfiguration.id === config.id) {
      MAP_CTX.lineWidth = 0.6;
    } else {
      MAP_CTX.lineWidth = 0.3;
    }
    for (let j = 0; j < config.nodes.length; j++) {
      const node = config.nodes[j];
      const diff = {
        x: sw.position.x - node.position.x,
        y: sw.position.z - node.position.z
      };
      const mag = Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2));
      diff.x = 3 * -diff.x / mag;
      diff.y = 3 * -diff.y / mag;
      MAP_CTX.beginPath();
      MAP_CTX.moveTo(0, 0);
      MAP_CTX.lineTo(diff.x, diff.y);
      MAP_CTX.stroke();
    }
  }
}

function drawSwitch() {
  MAP_CTX.fillStyle = `rgb(245, 188, 66)`;
  MAP_CTX.strokeStyle = `rgb(245, 188, 66)`;
  MAP_CTX.lineWidth = 0.2;
  circle(MAP_CTX, 0, 0.3, 0.2);
  MAP_CTX.fill();
  circle(MAP_CTX, -0.3, -0.3, 0.2);
  MAP_CTX.fill();
  circle(MAP_CTX, 0.3, -0.3, 0.2);
  MAP_CTX.fill();
  MAP_CTX.beginPath();
  MAP_CTX.moveTo(0, 0.3);
  MAP_CTX.lineTo(0, 0);
  MAP_CTX.lineTo(0.3, -0.3);
  MAP_CTX.moveTo(0, 0);
  MAP_CTX.lineTo(-0.3, -0.3);
  MAP_CTX.stroke();
}

function drawLabel(lbl) {
  MAP_CTX.fillStyle = "black";
  circle(MAP_CTX, 0, 0, 0.1);
  MAP_CTX.fill();
  MAP_CTX.strokeStyle = "black";
  MAP_CTX.font = "0.5px Sans-Serif";
  MAP_CTX.fillText(lbl.text, 0.1, -0.2);
}

function drawOnlineIndicator(component) {
  MAP_CTX.lineWidth = 0.1;
  if (component.online) {
    MAP_CTX.fillStyle = `rgba(52, 174, 235, 128)`;
    MAP_CTX.strokeStyle = `rgba(52, 174, 235, 128)`;
  } else {
    MAP_CTX.fillStyle = `rgba(153, 153, 153, 128)`;
    MAP_CTX.strokeStyle = `rgba(153, 153, 153, 128)`;
  }
  MAP_CTX.beginPath();
  MAP_CTX.arc(0, 0.2, 0.125, 0, Math.PI * 2);
  MAP_CTX.fill();
  for (let r = 0; r < 3; r++) {
    MAP_CTX.beginPath();
    MAP_CTX.arc(0, 0, 0.1 + 0.2 * r, 7 * Math.PI / 6, 11 * Math.PI / 6);
    MAP_CTX.stroke();
  }
}

function drawConnectedNodes(worldTx, component) {
  const s = camScale();
  MAP_CTX.lineWidth = 5 / s;
  MAP_CTX.strokeStyle = "black";
  for (let i = 0; i < component.connectedNodes.length; i++) {
    const node = component.connectedNodes[i];
    MAP_CTX.beginPath();
    MAP_CTX.moveTo(component.position.x, component.position.z);
    MAP_CTX.lineTo(node.position.x, node.position.z);
    MAP_CTX.stroke();
  }
}
