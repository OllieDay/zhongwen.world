<template>
  <nav class="uk-navbar-container uk-margin" uk-navbar>
    <div class="uk-navbar-left">
      <Logo/>
    </div>
  </nav>
</template>

<script>
import Logo from "./Logo.vue";
export default {
  name: "Navigation",
  components: {
    Logo
  }
};
</script>
