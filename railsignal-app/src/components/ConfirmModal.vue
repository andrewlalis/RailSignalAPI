<template>
  <div class="modal fade" tabindex="-1" :id="id">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{title}}</h5>
        </div>
        <div class="modal-body">
          <p>{{message}}</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" :id="id + '_cancel'">Cancel</button>
          <button type="button" class="btn btn-primary" :id="id + '_yes'">Yes</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {Modal} from "bootstrap";

export default {
  name: "ConfirmModal",
  props: {
    id: {
      type: String,
      required: true
    },
    message: {
      type: String,
      required: false,
      default: "Are you sure you want to continue?"
    },
    title: {
      type: String,
      required: false,
      default: "Confirm"
    }
  },
  expose: ['showConfirm'],
  methods: {
    showConfirm() {
      return new Promise(resolve => {
        console.log(this.id);
        console.log(this.title);
        const modalElement = document.getElementById(this.id);
        const modal = new Modal(modalElement);
        console.log(modal);

        function onDismiss() {
          modal.hide();
        }

        function onYes() {
          modalElement.addEventListener("hidden.bs.modal", function onSuccess() {
            modalElement.removeEventListener("hidden.bs.modal", onSuccess);
            resolve();
          });
          modal.hide();
        }

        const cancelButton = document.getElementById(this.id + "_cancel");
        cancelButton.removeEventListener("click", onDismiss);
        cancelButton.addEventListener("click", onDismiss)
        const yesButton = document.getElementById(this.id + "_yes");
        yesButton.removeEventListener("click", onYes);
        yesButton.addEventListener("click", onYes);
        modal.show(modalElement);
      });
    }
  }
}
</script>

<style scoped>
</style>