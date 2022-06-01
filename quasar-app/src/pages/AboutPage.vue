<template>
  <q-page class="q-pa-md">
    <div class="row">
      <div class="col-md-6">
        <div class="text-h2">About Rail Signal</div>
        <hr>
        <p>
          This application was developed by <a href="https://andrewlalis.github.io">Andrew Lalis</a>
          after several years of tinkering with various ways to automate rail
          networks in Minecraft. However, Rail Signal was designed from the
          ground up to be free from the constraints of any particular system.
          <em>Theoretically</em>, you could use Rail Signal to manage a real-world
          rail network, although I wouldn't recommend it, due to the complex and
          secure nature of real-world networks.
        </p>
        <p>
          Of course, Rail Signal was originally designed for Minecraft, so we've
          started by adding Rail Signal compatible drivers for Computercraft
          and Immersive Railroading by default. If you have a different system
          and would like to integrate it with Rail Signal, please <a href="https://github.com/andrewlalis/RailSignalAPI/issues" target="_blank">create a new issue</a>
          on the GitHub repository.
        </p>
        <div class="text-h4"><q-icon :name="fasMicrochip"/> Technologies</div>
        <p>
          This web app was built using <a href="https://quasar.dev">Quasar</a>
          and <a href="https://vuejs.org/">VueJS</a>. The API that powers this
          application was built using <a href="https://spring.io/">Spring</a>
          and Java 17. For more technical information, please visit Rail Signal's
          <a href="https://github.com/andrewlalis/RailSignalAPI">GitHub repository</a>.
        </p>
        <div class="text-h4"><q-icon :name="fasHeart"/> Support</div>
        <p>
          If you're enjoying this app, please consider making a <a href="https://paypal.me/andrewlalis" target="_blank">donation to
          Andrew's work on paypal</a>.
        </p>
      </div>
    </div>
  </q-page>
</template>

<script>
import {fasHeart, fasMicrochip} from "@quasar/extras/fontawesome-v6";

export default {
  name: "AboutPage",
  setup() {
    return {fasHeart, fasMicrochip};
  }
};
</script>

<style scoped>

</style>
