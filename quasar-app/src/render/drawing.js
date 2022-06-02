/*
Helper functions to actually perform rendering of different components.
*/

import { getScaleFactor, getWorldTransform, isComponentHovered, isComponentSelected } from "./mapRenderer";
import { circle, roundedRect, sortPoints } from "./canvasUtils";
import randomColor from "randomcolor";

export function drawMap(ctx, rs) {
  const worldTx = getWorldTransform();
  ctx.setTransform(worldTx);
  drawSegments(ctx, rs);
  drawNodeConnections(ctx, rs, worldTx);
  drawComponents(ctx, rs, worldTx);
}

function drawSegments(ctx, rs) {
  const segmentPoints = new Map();
  // Gather for each segment a set of points representing its bounds.
  rs.segments.forEach(segment => segmentPoints.set(segment.id, []));
  for (let i = 0; i < rs.components.length; i++) {
    const c = rs.components[i];
    if (c.type === "SEGMENT_BOUNDARY") {
      for (let j = 0; j < c.segments.length; j++) {
        segmentPoints.get(c.segments[j].id).push({x: c.position.x, y: c.position.z});
      }
    }
  }
  // Sort the points to make regular convex polygons.
  for (let i = 0; i < rs.segments.length; i++) {
    const unsortedPoints = segmentPoints.get(rs.segments[i].id);
    segmentPoints.set(rs.segments[i].id, sortPoints(unsortedPoints));
  }

  for (let i = 0; i < rs.segments.length; i++) {
    const color = randomColor({ luminosity: 'light', format: 'rgb', seed: rs.segments[i].id });
    ctx.fillStyle = color;
    ctx.strokeStyle = color;
    ctx.lineWidth = 5;
    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    ctx.font = "3px Sans-Serif";

    const points = segmentPoints.get(rs.segments[i].id);
    if (points.length === 0) continue;
    const avgPoint = {x: points[0].x, y: points[0].y};
    if (points.length === 1) {
      circle(ctx, points[0].x, points[0].y, 5);
      ctx.fill();
    } else {
      ctx.beginPath();
      ctx.moveTo(points[0].x, points[0].y);
      for (let j = 1; j < points.length; j++) {
        ctx.lineTo(points[j].x, points[j].y);
        avgPoint.x += points[j].x;
        avgPoint.y += points[j].y;
      }
      avgPoint.x /= points.length;
      avgPoint.y /= points.length;
      ctx.lineTo(points[0].x, points[0].y);
      ctx.fill();
      ctx.stroke();
    }

    // Draw the segment name.
    ctx.fillStyle = randomColor({luminosity: 'dark', format: 'rgb', seed: rs.segments[i].id});
    ctx.fillText(rs.segments[i].name, avgPoint.x, avgPoint.y);
  }
}

function drawNodeConnections(ctx, rs, worldTx) {
  for (let i = 0; i < rs.components.length; i++) {
    const c = rs.components[i];
    if (c.connectedNodes !== undefined && c.connectedNodes !== null) {
      drawConnectedNodes(ctx, worldTx, c);
    }
  }
}

function drawComponents(ctx, rs, worldTx) {
  // Draw switch configurations first
  for (let i = 0; i < rs.components.length; i++) {
    if (rs.components[i].type === "SWITCH") {
      drawSwitchConfigurations(ctx, worldTx, rs.components[i]);
    }
  }

  for (let i = 0; i < rs.components.length; i++) {
    drawComponent(ctx, worldTx, rs.components[i]);
  }
}

function drawComponent(ctx, worldTx, component) {
    const tx = DOMMatrix.fromMatrix(worldTx);
    tx.translateSelf(component.position.x, component.position.z, 0);
    const s = getScaleFactor();
    tx.scaleSelf(1/s, 1/s, 1/s);
    tx.scaleSelf(20, 20, 20);
    ctx.setTransform(tx);

    if (component.type === "SIGNAL") {
        drawSignal(ctx, component);
    } else if (component.type === "SEGMENT_BOUNDARY") {
        drawSegmentBoundary(ctx, component);
    } else if (component.type === "SWITCH") {
        drawSwitch(ctx, component);
    } else if (component.type === "LABEL") {
      drawLabel(ctx, component);
    }

    ctx.setTransform(tx.translate(0.75, -0.75));
    if (component.online !== undefined && component.online !== null) {
        drawOnlineIndicator(ctx, component);
    }

    ctx.setTransform(tx);
    // Draw hovered status.
    if (isComponentHovered(component) || isComponentSelected(component)) {
        ctx.fillStyle = `rgba(255, 255, 0, 0.5)`;
        circle(ctx, 0, 0, 0.75);
        ctx.fill();
    }
}

function drawSignal(ctx, signal) {
    roundedRect(ctx, -0.3, -0.5, 0.6, 1, 0.25);
    ctx.fillStyle = "black";
    ctx.fill();
    if (signal.segment) {
      if (signal.segment.occupied === true) {
        ctx.fillStyle = `rgb(255, 0, 0)`;
      } else if (signal.segment.occupied === false) {
        ctx.fillStyle = `rgb(0, 255, 0)`;
      } else {
        ctx.fillStyle = `rgb(255, 255, 0)`;
      }
    } else {
      ctx.fillStyle = `rgb(0, 0, 255)`;
    }
    circle(ctx, 0, -0.2, 0.15);
    ctx.fill();
}

function drawSegmentBoundary(ctx) {
    ctx.fillStyle = `rgb(150, 58, 224)`;
    ctx.beginPath();
    ctx.moveTo(0, -0.5);
    ctx.lineTo(-0.5, 0);
    ctx.lineTo(0, 0.5);
    ctx.lineTo(0.5, 0);
    ctx.lineTo(0, -0.5);
    ctx.fill();
}

function drawSwitchConfigurations(ctx, worldTx, sw) {
  const tx = DOMMatrix.fromMatrix(worldTx);
  tx.translateSelf(sw.position.x, sw.position.z, 0);
  const s = getScaleFactor();
  tx.scaleSelf(1/s, 1/s, 1/s);
  tx.scaleSelf(20, 20, 20);
  ctx.setTransform(tx);

  for (let i = 0; i < sw.possibleConfigurations.length; i++) {
    const config = sw.possibleConfigurations[i];
    ctx.strokeStyle = randomColor({
      seed: config.id,
      format: 'rgb',
      luminosity: 'bright'
    });
    if (sw.activeConfiguration !== null && sw.activeConfiguration.id === config.id) {
      ctx.lineWidth = 0.6;
    } else {
      ctx.lineWidth = 0.3;
    }
    for (let j = 0; j < config.nodes.length; j++) {
      const node = config.nodes[j];
      const diff = {
        x: sw.position.x - node.position.x,
        y: sw.position.z - node.position.z,
      };
      const mag = Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2));
      diff.x = 3 * -diff.x / mag;
      diff.y = 3 * -diff.y / mag;
      ctx.beginPath();
      ctx.moveTo(0, 0);
      ctx.lineTo(diff.x, diff.y);
      ctx.stroke();
    }
  }
}

function drawSwitch(ctx, sw) {
  ctx.fillStyle = `rgb(245, 188, 66)`;
  ctx.strokeStyle = `rgb(245, 188, 66)`;
  ctx.lineWidth = 0.2;
  circle(ctx, 0, 0.3, 0.2);
  ctx.fill();
  circle(ctx, -0.3, -0.3, 0.2);
  ctx.fill();
  circle(ctx, 0.3, -0.3, 0.2);
  ctx.fill();
  ctx.beginPath();
  ctx.moveTo(0, 0.3);
  ctx.lineTo(0, 0);
  ctx.lineTo(0.3, -0.3);
  ctx.moveTo(0, 0);
  ctx.lineTo(-0.3, -0.3);
  ctx.stroke();
}

function drawLabel(ctx, lbl) {
  ctx.fillStyle = "black";
  circle(ctx, 0, 0, 0.1);
  ctx.fill();
  ctx.strokeStyle = "black";
  ctx.font = "0.5px Sans-Serif";
  ctx.fillText(lbl.text, 0.1, -0.2);
}

function drawOnlineIndicator(ctx, component) {
    ctx.lineWidth = 0.1;
    if (component.online) {
        ctx.fillStyle = `rgba(52, 174, 235, 128)`;
        ctx.strokeStyle = `rgba(52, 174, 235, 128)`;
    } else {
        ctx.fillStyle = `rgba(153, 153, 153, 128)`;
        ctx.strokeStyle = `rgba(153, 153, 153, 128)`;
    }
    ctx.beginPath();
    ctx.arc(0, 0.2, 0.125, 0, Math.PI * 2);
    ctx.fill();
    for (let r = 0; r < 3; r++) {
        ctx.beginPath();
        ctx.arc(0, 0, 0.1 + 0.2 * r, 7 * Math.PI / 6, 11 * Math.PI / 6);
        ctx.stroke();
    }
}

export function drawConnectedNodes(ctx, worldTx, component) {
    const s = getScaleFactor();
    ctx.lineWidth = 5 / s;
    ctx.strokeStyle = "black";
    for (let i = 0; i < component.connectedNodes.length; i++) {
        const node = component.connectedNodes[i];
        ctx.beginPath();
        ctx.moveTo(component.position.x, component.position.z);
        ctx.lineTo(node.position.x, node.position.z);
        ctx.stroke();
    }
}
