<template>
  <form>
    <label for="addSegmentName">Name</label>
    <input type="text" v-model="formData.segmentName" />
    <button>Add</button>
  </form>
</template>

<script>
export default {
  name: "AddSegment",
  data() {
    return {
      formData: {
        segmentName: ""
      }
    }
  }
}
</script>

<style scoped>

</style>