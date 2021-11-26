const $ = jQuery;

let railSystemSelect;
let railMapCanvas;
let railSystem = null;
let detailPanel = null;

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

    $('#addRailSystemInput').on("input", () => {
        $('#addRailSystemButton').prop("disabled", $('#addRailSystemInput').val() === "");
    });
    $('#addRailSystemButton').click(addRailSystem);
    $('#removeRailSystemButton').click(deleteRailSystem);

    detailPanel = $('#railMapDetailPanel');

    refreshRailSystems(true);
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
    detailPanel.empty();
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
            window.setInterval(railSystemUpdated, 1000);
        });
    $.get("/api/railSystems/" + railSystem.id + "/branches")
        .done(branches => {
            railSystem.branches = branches;
            const branchSelects = $('.js_branch_list');
            branchSelects.empty();
            railSystem.branches.forEach(branch => {
                branchSelects.append($('<option value="' + branch.name + '"></option>'))
            });
        });
}

// Updates the current rail system's information from the API.
function railSystemUpdated() {
    $.get("/api/railSystems/" + railSystem.id + "/signals")
        .done(signals => {
            railSystem.signals = signals;
            drawRailSystem();
        });
    $.get("/api/railSystems/" + railSystem.id + "/branches")
        .done(branches => {
            railSystem.branches = branches;
            const branchSelects = $('.js_branch_list');
            branchSelects.empty();
            railSystem.branches.forEach(branch => {
                branchSelects.append($('<option value="' + branch.name + '"></option>'))
            });
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
    detailPanel.empty();
    if (signal !== null) {
        const tpl = Handlebars.compile($('#signalTemplate').html());
        detailPanel.html(tpl(signal));
        signal.branchConnections.forEach(con => {
            const select = $('#signalPotentialConnectionsSelect-' + con.id);
            $.get("/api/railSystems/" + railSystem.id + "/branches/" + con.branch.id + "/signals")
                .done(signals => {
                    signals = signals.filter(s => s.id !== signal.id);
                    let connections = [];
                    signals.forEach(s => {
                        s.branchConnections
                            .filter(c => c.branch.id === con.branch.id && !con.reachableSignalConnections.some(rc => rc.connectionId === c.id))
                            .forEach(potentialConnection => {
                                potentialConnection.signalName = s.name;
                                potentialConnection.signalId = s.id;
                                connections.push(potentialConnection);
                            });
                    });
                    select.empty();
                    const row = $('#signalPotentialConnectionsRow-' + con.id);
                    row.toggle(connections.length > 0);
                    connections.forEach(c => {
                        select.append($(`<option value="${c.id}">${c.signalName} via ${c.direction} connection ${c.id}</option>`))
                    });
                });
        });
    }
}

function addRailSystem() {
    let name = $('#addRailSystemInput').val().trim();
    $.post({
        url: "/api/railSystems",
        data: JSON.stringify({name: name}),
        contentType: "application/json"
    })
        .done((response) => {
            refreshRailSystems();
        })
        .always(() => {
            $('#addRailSystemInput').val("");
        });
}

function deleteRailSystem() {
    if (railSystem !== null && railSystem.id) {
        confirm("Are you sure you want to permanently remove rail system " + railSystem.id + "?")
            .then(() => {
                $.ajax({
                    url: "/api/railSystems/" + railSystem.id,
                    type: "DELETE"
                })
                    .always(() => {
                        refreshRailSystems(true);
                    });
            });
    }
}

function refreshRailSystems(selectFirst) {
    $.get("/api/railSystems")
        .done(railSystems => {
            railSystemSelect.empty();
            railSystems.forEach(railSystem => {
                let option = $(`<option value="${railSystem.id}">${railSystem.name} - ID: ${railSystem.id}</option>`)
                railSystemSelect.append(option);
            });
            if (selectFirst) {
                railSystemSelect.val(railSystems[0].id);
                railSystemSelect.change();
            }
        });
}

function addNewSignal() {
    const modalElement = $('#addSignalModal');
    const form = $('#addSignalForm');
    form.validate();
    if (!form.valid()) return;
    const data = {
        name: $('#addSignalName').val().trim(),
        position: {
            x: $('#addSignalPositionX').val(),
            y: $('#addSignalPositionY').val(),
            z: $('#addSignalPositionZ').val()
        },
        branchConnections: [
            {
                direction: $('#addSignalFirstConnectionDirection').val(),
                name: $('#addSignalFirstConnectionBranch').val()
            },
            {
                direction: $('#addSignalSecondConnectionDirection').val(),
                name: $('#addSignalSecondConnectionBranch').val()
            }
        ]
    };
    const modal = bootstrap.Modal.getInstance(modalElement[0]);
    modal.hide();
    modalElement.on("hidden.bs.modal", () => {
        confirm("Are you sure you want to add this new signal to the system?")
            .then(() => {
                $.post({
                    url: "/api/railSystems/" + railSystem.id + "/signals",
                    data: JSON.stringify(data),
                    contentType: "application/json"
                })
                    .done(() => {
                        form.trigger("reset");
                        railSystemUpdated();
                    })
                    .fail((response) => {
                        console.error(response);
                        $('#addSignalAlertsContainer').append($('<div class="alert alert-danger">An error occurred.</div>'));
                        modal.show();
                    });
            })
            .catch(() => {
                form.trigger("reset");
            });
    });
}

function removeSignal(signalId) {
    confirm(`Are you sure you want to remove signal ${signalId}? This cannot be undone.`)
        .then(() => {
            $.ajax({
                url: `/api/railSystems/${railSystem.id}/signals/${signalId}`,
                type: "DELETE"
            })
                .always(() => {
                    railSystemUpdated();
                })
                .fail((response) => {
                    console.error(response);
                })
        })
}

function removeReachableConnection(signalId, fromId, toId) {
    confirm(`Are you sure you want to remove the connection from ${fromId} to ${toId} from signal ${signalId}?`)
        .then(() => {
            $.get(`/api/railSystems/${railSystem.id}/signals/${signalId}`)
                .done(signal => {
                    let connections = [];
                    signal.branchConnections.forEach(con => {
                        con.reachableSignalConnections.forEach(reachableCon => {
                            connections.push({from: con.id, to: reachableCon.connectionId});
                        });
                    });
                    connections = connections.filter(c => !(c.from === fromId && c.to === toId));
                    $.post({
                        url: `/api/railSystems/${railSystem.id}/signals/${signal.id}/signalConnections`,
                        data: JSON.stringify({connections: connections}),
                        contentType: "application/json"
                    })
                        .done(() => {
                            railSystemUpdated();
                        })
                        .fail((response) => {
                            console.error(response);
                        });
                });
        });
}

function addReachableConnectionBtn(signalId, fromId) {
    const select = $('#signalPotentialConnectionsSelect-' + fromId);
    const toId = select.val();
    if (toId) {
        addReachableConnection(signalId, fromId, toId);
    }
}

function addReachableConnection(signalId, fromId, toId) {
    confirm(`Are you sure you want to add a connection from ${fromId} to ${toId} from signal ${signalId}?`)
        .then(() => {
            $.get(`/api/railSystems/${railSystem.id}/signals/${signalId}`)
                .done(signal => {
                    let connections = [];
                    signal.branchConnections.forEach(con => {
                        con.reachableSignalConnections.forEach(reachableCon => {
                            connections.push({from: con.id, to: reachableCon.connectionId});
                        });
                    });
                    if (!connections.find(c => c.from === fromId && c.to === toId)) {
                        connections.push({from: fromId, to: toId});
                        $.post({
                            url: `/api/railSystems/${railSystem.id}/signals/${signal.id}/signalConnections`,
                            data: JSON.stringify({connections: connections}),
                            contentType: "application/json"
                        })
                            .done(() => {
                                railSystemChanged();
                            })
                            .fail((response) => {
                                console.error(response);
                            });
                    }
                });
        });
}

function confirm(message) {
    const modalElement = $('#confirmModal');
    if (message) {
        $('#confirmModalBody').html(message);
    } else {
        $('#confirmModalBody').html("Are you sure you want to continue?");
    }
    const modal = new bootstrap.Modal(modalElement[0], {keyboard: false});
    modal.show();
    return new Promise((resolve, reject) => {
        $('#confirmModalOkButton').click(() => {
            modalElement.on("hidden.bs.modal", () => resolve());
        });
        $('#confirmModalCancelButton').click(() => {
            modalElement.on("hidden.bs.modal", () => reject());
        });
    });
}
