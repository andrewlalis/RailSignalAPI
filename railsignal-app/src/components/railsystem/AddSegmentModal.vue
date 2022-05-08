<template>
  <div class="modal fade" tabindex="-1" id="addSegmentModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Add Segment</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <p>
            Add a new segment to this rail system. A <em>segment</em> is the
            basic organizational unit of any rail system. It is a section of
            the network that signals can monitor, and <em>segment boundary nodes</em>
            define the extent of the segment, and monitor trains entering and
            leaving the segment.
          </p>
          <p>
            You can think of a segment as a single, secure block of of the rail
            network that only one train may pass through at once. For example,
            a junction or station siding.
          </p>
          <form>
            <label for="addSegmentName" class="form-label">Name</label>
            <input
                id="addSegmentName"
                class="form-control"
                type="text"
                v-model="formData.name"
                required
            />
          </form>
          <div v-if="warnings.length > 0">
            <div v-for="msg in warnings" :key="msg" class="alert alert-danger mt-2">
              {{msg}}
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" @click="formSubmitted()">Add</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {useRailSystemsStore} from "../../stores/railSystemsStore";
import {Modal} from "bootstrap";

export default {
  name: "AddSegmentModal",
  setup() {
    const rsStore = useRailSystemsStore();
    return {
      rsStore
    };
  },
  data() {
    return {
      formData: {
        name: ""
      },
      warnings: []
    };
  },
  methods: {
    formSubmitted() {
      const modal = Modal.getInstance(document.getElementById("addSegmentModal"));
      this.rsStore.addSegment(this.formData.name)
          .then(() => {
            this.formData.name = "";
            modal.hide();
          })
          .catch(error => {
            this.warnings.length = 0;
            this.warnings.push("Couldn't add the segment: " + error.response.data.message)
          });
    }
  }
}
</script>

<style scoped>

</style>