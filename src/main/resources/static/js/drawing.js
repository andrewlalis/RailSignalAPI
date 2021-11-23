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

function mousePointToWorld(event) {
    const rect = railMapCanvas[0].getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    return worldTransform().invertSelf().transformPoint(new DOMPoint(x, y, 0, 1));
}

function signalTransform(worldTx, signal) {
    let tx = DOMMatrix.fromMatrix(worldTx);
    tx.translateSelf(signal.position.x, signal.position.z, 0);
    let direction = signal.branchConnections[0].direction;
    if (direction === undefined || direction === null || direction === "") {
        direction = "NORTH";
    }
    let angle = 0;
    if (direction === "NORTH" || direction === "SOUTH") {
        angle = 90;
    } else if (direction === "NORTH_WEST" || direction === "SOUTH_EAST") {
        angle = 45;
    } else if (direction === "NORTH_EAST" || direction === "SOUTH_WEST") {
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
        console.log('printing!')
        console.log(element);
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
    let firstCon = signal.branchConnections[0];
    let secondCon = signal.branchConnections[1];
    if (firstCon === "EAST" || firstCon === "SOUTH" || firstCon === "SOUTH_WEST" || firstCon === "SOUTH_EAST") {
        let tmp = firstCon;
        firstCon = secondCon;
        secondCon = tmp;
    }

    ctx.fillStyle = getSignalColor(signal, firstCon.branch.status);
    ctx.fillRect(-0.75, -0.4, 0.3, 0.8);
    ctx.fillStyle = getSignalColor(signal, secondCon.branch.status);
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

function drawReachableConnections(ctx, signal) {
    ctx.strokeStyle = 'black';
    ctx.lineWidth = 0.25;
    signal.branchConnections.forEach(connection => {
        connection.reachableSignalConnections.forEach(reachableCon => {
            ctx.beginPath();
            ctx.moveTo(signal.position.x, signal.position.z);
            ctx.lineTo(reachableCon.signalPosition.x, reachableCon.signalPosition.z);
            ctx.stroke();
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