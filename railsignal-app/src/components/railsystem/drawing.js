/*
Helper functions to actually perform rendering of different components.
*/

import {getScaleFactor, isComponentHovered} from "./mapRenderer";
import {roundedRect, circle} from "./canvasUtils";

export function drawComponent(ctx, worldTx, component) {
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
    }

    ctx.setTransform(tx.translate(0.75, -0.75));
    if (component.online !== undefined && component.online !== null) {
        drawOnlineIndicator(ctx, component);
    }

    ctx.setTransform(tx);
    // Draw hovered status.
    if (isComponentHovered(component)) {
        ctx.fillStyle = `rgba(255, 255, 0, 0.5)`;
        circle(ctx, 0, 0, 0.75);
        ctx.fill();
    }
}

function drawSignal(ctx, signal) {
    roundedRect(ctx, -0.3, -0.5, 0.6, 1, 0.25);
    ctx.fillStyle = "black";
    ctx.fill();
    ctx.fillStyle = "rgb(0, 255, 0)";
    circle(ctx, 0, -0.2, 0.15);
    ctx.fill();
}

function drawSegmentBoundary(ctx, segmentBoundary) {
    ctx.fillStyle = `rgb(150, 58, 224)`;
    ctx.beginPath();
    ctx.moveTo(0, -0.5);
    ctx.lineTo(-0.5, 0);
    ctx.lineTo(0, 0.5);
    ctx.lineTo(0.5, 0);
    ctx.lineTo(0, -0.5);
    ctx.fill();
}

function drawSwitch(ctx, sw) {
    const colors = [
        `rgba(61, 148, 66, 0.25)`,
        `rgba(59, 22, 135, 0.25)`,
        `rgba(145, 17, 90, 0.25)`,
        `rgba(191, 49, 10, 0.25)`
    ];
    ctx.lineWidth = 1;
    for (let i = 0; i < sw.possibleConfigurations.length; i++) {
        const config = sw.possibleConfigurations[i];
        ctx.strokeStyle = colors[i];
        for (let j = 0; j < config.nodes.length; j++) {
            const node = config.nodes[j];
            const diff = {
                x: sw.position.x - node.position.x,
                y: sw.position.z - node.position.z,
            };
            const mag = Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2));
            diff.x = 2 * -diff.x / mag;
            diff.y = 2 * -diff.y / mag;
            ctx.beginPath();
            ctx.moveTo(0, 0);
            ctx.lineTo(diff.x, diff.y);
            ctx.stroke();
        }
    }
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
