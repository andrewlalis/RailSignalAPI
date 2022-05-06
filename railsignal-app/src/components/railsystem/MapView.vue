<script>
export default {
    props: {
        railSystem: {
            type: Object,
            required: true
        }
    },
    data() {
        return {}
    }
}
</script>

<template>
    <canvas>
        Your browser doesn't support canvas!
    </canvas>
</template>

<style>
canvas {
  border: 1px solid black;
  width: 70%;
}
</style>