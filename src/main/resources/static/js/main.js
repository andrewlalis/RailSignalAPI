const $ = jQuery;

let railSystemSelect;
let railMapCanvas;
let railSystem = null;

let canvasTranslation = {x: 0, y: 0};
let canvasDragOrigin = null;
let canvasDragTranslation = null;
let hoveredElements = [];

const SCALE_VALUES = [0.01, 0.1, 0.25, 0.5, 1.0, 1.25, 1.5, 2.0, 3.0, 4.0, 6.0, 8.0, 10.0, 12.0, 16.0, 20.0, 30.0, 45.0, 60.0, 80.0, 100.0];
const SCALE_INDEX_NORMAL = 7;
let canvasScaleIndex = SCALE_INDEX_NORMAL;

$(document).ready(() => {
    railSystemSelect = $('#railSystemSelect');
    railSystemSelect.change(railSystemChanged);

    railMapCanvas = $('#railMapCanvas');
    railMapCanvas.on('wheel', onCanvasMouseWheel);
    railMapCanvas.mousedown(onCanvasMouseDown);
    railMapCanvas.mouseup(onCanvasMouseUp);
    railMapCanvas.mousemove(onCanvasMouseMove);

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

// Handle mouse scrolling within the context of the canvas.
function onCanvasMouseWheel(event) {
    let s = event.originalEvent.deltaY;
    if (s > 0) {
        canvasScaleIndex = Math.max(0, canvasScaleIndex - 1);
    } else if (s < 0) {
        canvasScaleIndex = Math.min(SCALE_VALUES.length - 1, canvasScaleIndex + 1);
    }
    drawRailSystem();
    event.stopPropagation();
}

// Handle mouse clicks on the canvas.
function onCanvasMouseDown(event) {
    const p = getMousePoint(event);
    canvasDragOrigin = {x: p.x, y: p.y};
}

// Handle mouse release on the canvas, which stops dragging or indicates that the user may have clicked on something.
function onCanvasMouseUp(event) {
    if (canvasDragTranslation !== null) {
        canvasTranslation.x += canvasDragTranslation.x;
        canvasTranslation.y += canvasDragTranslation.y;
    } else {
        const p = getMousePoint(event);
        let signalClicked = false;
        railSystem.signals.forEach(signal => {
            const sp = new DOMPoint(signal.position.x, signal.position.z, 0, 1);
            const canvasSp = worldPointToCanvas(sp);
            const dist = Math.sqrt(Math.pow(p.x - canvasSp.x, 2) + Math.pow(p.y - canvasSp.y, 2));
            if (dist < 5) {
                console.log(signal);
                onSignalSelected(signal);
                signalClicked = true;
            }
        });
        if (!signalClicked) {
            onSignalSelected(null);
        }
    }
    canvasDragOrigin = null;
    canvasDragTranslation = null;
}

// Handle mouse motion over the canvas. This is for dragging and hovering over items.
function onCanvasMouseMove(event) {
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
        const p = getMousePoint(event);
        railSystem.signals.forEach(signal => {
            const sp = new DOMPoint(signal.position.x, signal.position.z, 0, 1);
            const canvasSp = worldPointToCanvas(sp);
            const dist = Math.sqrt(Math.pow(p.x - canvasSp.x, 2) + Math.pow(p.y - canvasSp.y, 2));
            if (dist < 5) {
                hoveredElements.push(signal);
            }
        });
        drawRailSystem();
    }
}

function getMousePoint(event) {
    const rect = railMapCanvas[0].getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    return new DOMPoint(x, y, 0, 1);
}

function railSystemChanged() {
    railSystem = {};
    railSystem.id = railSystemSelect.val();
    $.get("/api/railSystems/" + railSystem.id + "/signals")
        .done(signals => {
            railSystem.signals = signals;
            let bb = getRailSystemBoundingBox();
            canvasTranslation.x = -1 * (bb.x + (bb.width / 2));
            canvasTranslation.y = -1 * (bb.y + (bb.height / 2));
            canvasScaleIndex = SCALE_INDEX_NORMAL;
            drawRailSystem();
        });
    $.get("/api/railSystems/" + railSystem.id + "/branches")
        .done(branches => {
            railSystem.branches = branches;
        });
}

function selectSignalById(id) {
    railSystem.signals.forEach(signal => {
        if (signal.id === id) {
            onSignalSelected(signal);
        }
    });
}

function onSignalSelected(signal) {
    const dp = $('#railMapDetailPanel');
    dp.empty();
    if (signal !== null) {
        const tpl = Handlebars.compile($('#signalTemplate').html());
        dp.html(tpl(signal));
    }
}
