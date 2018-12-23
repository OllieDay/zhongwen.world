<template>
  <div id="app">
    <Navigation/>
    <Content/>
  </div>
</template>

<script>
import "uikit/dist/css/uikit.min.css";
import UIkit from "uikit";
import UIkitIcons from "uikit/dist/js/uikit-icons";
import Navigation from "./components/Navigation.vue";
import Content from "./components/Content.vue";

export default {
  name: "app",
  components: {
    Navigation,
    Content
  },
  beforeCreate: function() {
    UIkit.use(UIkitIcons);
  }
};
</script>
