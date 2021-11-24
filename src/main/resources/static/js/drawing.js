function worldTransform() {
    const canvasRect = railMapCanvas[0].getBoundingClientRect();
    const scale = SCALE_VALUES[canvasScaleIndex];
    let tx = new DOMMatrix();
    tx.translateSelf(canvasRect.width / 2, canvasRect.height / 2, 0);
    tx.scaleSelf(scale, scale, scale);
    tx.translateSelf(canvasTranslation.x, canvasTranslation.y, 0);
    if (canvasDragOrigin !== null && canvasDragTranslation !== null) {
        tx.translateSelf(canvasDragTranslation.x, canvasDragTranslation.y, 0);
    }
    return tx;
}

function canvasPointToWorld(p) {
    return worldTransform().invertSelf().transformPoint(p);
}

function worldPointToCanvas(p) {
    return worldTransform().transformPoint(p);
}

function signalTransform(worldTx, signal) {
    let tx = DOMMatrix.fromMatrix(worldTx);
    tx.translateSelf(signal.position.x, signal.position.z, 0);
    let direction = signal.branchConnections[0].direction;
    if (direction === "EAST" || direction === "SOUTH" || direction === "SOUTH_EAST" || direction === "SOUTH_WEST") {
        direction = signal.branchConnections[1].direction;
    }
    if (direction === undefined || direction === null || direction === "") {
        direction = "NORTH";
    }
    let angle = 0;
    if (direction === "NORTH") {
        angle = 90;
    } else if (direction === "NORTH_WEST") {
        angle = 45;
    } else if (direction === "NORTH_EAST") {
        angle = 135;
    }
    tx.rotateSelf(0, 0, angle);
    return tx;
}

function drawRailSystem() {
    let ctx = railMapCanvas[0].getContext("2d");
    ctx.resetTransform();
    ctx.clearRect(0, 0, railMapCanvas.width(), railMapCanvas.height());
    const worldTx = worldTransform();
    ctx.setTransform(worldTx);
    railSystem.signals.forEach(signal => {
        drawReachableConnections(ctx, signal);
    });
    railSystem.signals.forEach(signal => {
        ctx.setTransform(signalTransform(worldTx, signal));
        drawSignal(ctx, signal);
    });
    ctx.resetTransform();
    ctx.fillStyle = 'black';
    ctx.strokeStyle = 'black';
    ctx.font = '24px Serif';
    let textLine = 0;
    hoveredElements.forEach(element => {
        ctx.strokeText(element.name, 10, 20 + textLine * 20);
        ctx.fillText(element.name, 10, 20 + textLine * 20);
        textLine += 1;
    });
}

function drawSignal(ctx, signal) {
    if (signal.online) {
        ctx.fillStyle = 'black';
    } else {
        ctx.fillStyle = 'gray';
    }
    ctx.scale(2, 2);
    ctx.fillRect(-0.5, -0.5, 1, 1);
    let northWesterlyCon = signal.branchConnections[0];
    let southEasterlyCon = signal.branchConnections[1];
    if (northWesterlyCon.direction === "EAST" || northWesterlyCon.direction === "SOUTH" || northWesterlyCon.direction === "SOUTH_WEST" || northWesterlyCon.direction === "SOUTH_EAST") {
        let tmp = northWesterlyCon;
        northWesterlyCon = southEasterlyCon;
        southEasterlyCon = tmp;
    }

    ctx.fillStyle = getSignalColor(signal, southEasterlyCon.branch.status);
    ctx.fillRect(-0.75, -0.4, 0.3, 0.8);
    ctx.fillStyle = getSignalColor(signal, northWesterlyCon.branch.status);
    ctx.fillRect(0.45, -0.4, 0.3, 0.8);
}

function getSignalColor(signal, branchStatus) {
    if (!signal.online) return 'rgb(0, 0, 255)';
    if (branchStatus === "FREE") {
        return 'rgb(0, 255, 0)';
    } else if (branchStatus === "OCCUPIED") {
        return 'rgb(255, 0, 0)';
    } else {
        return 'rgb(0, 0, 255)';
    }
}

// Draws lines indicating reachable paths between this signal and others, with arrows for directionality.
function drawReachableConnections(ctx, signal) {
    ctx.strokeStyle = 'black';
    ctx.lineWidth = 0.25;
    signal.branchConnections.forEach(connection => {
        ctx.resetTransform();
        connection.reachableSignalConnections.forEach(reachableCon => {
            const dx = reachableCon.signalPosition.x - signal.position.x;
            const dy = reachableCon.signalPosition.z - signal.position.z;
            const dist = Math.sqrt(dx * dx + dy * dy);
            let tx = worldTransform();
            tx.translateSelf(signal.position.x, signal.position.z, 0);
            const angle = Math.atan2(dy, dx) * 180 / Math.PI - 90;
            tx.rotateSelf(0, 0, angle);
            ctx.setTransform(tx);
            ctx.beginPath();
            ctx.moveTo(0, 0);
            ctx.lineTo(0, dist);
            const arrowEnd = 5;
            const arrowWidth = 0.5;
            const arrowLength = 1;
            ctx.lineTo(0, arrowEnd);
            ctx.lineTo(arrowWidth, arrowEnd - arrowLength);
            ctx.lineTo(-arrowWidth, arrowEnd - arrowLength);
            ctx.lineTo(0, arrowEnd);
            ctx.stroke();
            ctx.fill();
        });
    });
}

function getRailSystemBoundingBox() {
    let min = {x: Number.MAX_SAFE_INTEGER, z: Number.MAX_SAFE_INTEGER};
    let max = {x: Number.MIN_SAFE_INTEGER, z: Number.MIN_SAFE_INTEGER};
    railSystem.signals.forEach(signal => {
        let p = signal.position;
        if (p.x < min.x) min.x = p.x;
        if (p.z < min.z) min.z = p.z;
        if (p.x > max.x) max.x = p.x;
        if (p.z > max.z) max.z = p.z;
    });
    return {x: min.x, y: min.z, width: Math.abs(max.x - min.x), height: Math.abs(max.z - min.z)};
}