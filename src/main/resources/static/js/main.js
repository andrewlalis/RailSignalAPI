const $ = jQuery;

let railSystemSelect;
let railMapCanvas;
let railSystem = null;

let canvasTranslation = {x: 0, y: 0};
let canvasDragOrigin = null;
let canvasDragTranslation = null;
let hoveredElements = [];

const SCALE_VALUES = [0.01, 0.1, 1.0, 1.25, 1.5, 2.0, 4.0, 6.0, 8.0, 10.0, 12.0, 16.0, 20.0, 30.0, 45.0, 60.0, 80.0, 100.0];
const SCALE_INDEX_NORMAL = 5;
let canvasScaleIndex = SCALE_INDEX_NORMAL;

$(document).ready(() => {
    railSystemSelect = $('#railSystemSelect');
    railSystemSelect.change(railSystemChanged);

    railMapCanvas = $('#railMapCanvas');
    railMapCanvas.on('wheel', onMouseWheel);
    railMapCanvas.mousedown(onMouseDown);
    railMapCanvas.mouseup(onMouseUp);
    railMapCanvas.mousemove(onMouseMove);

    $.get("/api/railSystems")
        .done(railSystems => {
            railSystems.forEach(railSystem => {
                let option = $('<option value="' + railSystem.id + '">' + railSystem.name + '</option>')
                railSystemSelect.append(option);
            });
            railSystemSelect.val(railSystems[0].id);
            railSystemSelect.change();
        });
});

function onMouseWheel(event) {
    let s = event.originalEvent.deltaY;
    if (s > 0) {
        canvasScaleIndex = Math.max(0, canvasScaleIndex - 1);
    } else if (s < 0) {
        canvasScaleIndex = Math.min(SCALE_VALUES.length - 1, canvasScaleIndex + 1);
    }
    drawRailSystem();
    event.stopPropagation();
}

function onMouseDown(event) {
    const rect = railMapCanvas[0].getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    canvasDragOrigin = {x: x, y: y};
}

function onMouseUp(event) {
    if (canvasDragTranslation !== null) {
        canvasTranslation.x += canvasDragTranslation.x;
        canvasTranslation.y += canvasDragTranslation.y;
    } else {
        const p = mousePointToWorld(event);
        railSystem.signals.forEach(signal => {
            const sp = new DOMPoint(signal.position.x, signal.position.z, 0, 1);
            const dist = Math.sqrt(Math.pow(p.x - sp.x, 2) + Math.pow(p.y - sp.y, 2));
            if (dist < 1) {
                console.log(signal);
                $('#testingText').val(JSON.stringify(signal, null, 2));
            }
        });
    }
    canvasDragOrigin = null;
    canvasDragTranslation = null;
}

function onMouseMove(event) {
    const rect = railMapCanvas[0].getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    if (canvasDragOrigin !== null) {
        const scale = SCALE_VALUES[canvasScaleIndex];
        const dx = x - canvasDragOrigin.x;
        const dy = y - canvasDragOrigin.y;
        canvasDragTranslation = {x: dx / scale, y: dy / scale};
        drawRailSystem();
    } else {
        hoveredElements = [];
        const p = mousePointToWorld(event);
        railSystem.signals.forEach(signal => {
            const sp = new DOMPoint(signal.position.x, signal.position.z, 0, 1);
            const dist = Math.sqrt(Math.pow(p.x - sp.x, 2) + Math.pow(p.y - sp.y, 2));
            if (dist < 1) {
                hoveredElements.push(signal);
            }
        });
        drawRailSystem();
    }
}

function railSystemChanged() {
    railSystem = {};
    railSystem.id = railSystemSelect.val();
    $.get("/api/railSystems/" + railSystem.id + "/signals")
        .done(signals => {
            railSystem.signals = signals;
            let bb = getRailSystemBoundingBox();
            // canvasTranslation.x = -1 * (bb.x + (bb.width / 2));
            // canvasTranslation.y = -1 * (bb.y + (bb.height / 2));
            // canvasScaleIndex = SCALE_INDEX_NORMAL;
            drawRailSystem();
            window.setTimeout(railSystemChanged, 1000);
        });
    $.get("/api/railSystems/" + railSystem.id + "/branches")
        .done(branches => {
            railSystem.branches = branches;
        });
}
