<template>
  <form id="search-form" class="uk-search uk-search-default uk-margin" @submit.prevent="search">
    <a @click="search" class="uk-search-icon-flip" uk-search-icon></a>
    <input class="uk-input" type="search" placeholder="Search..." v-model="terms">
  </form>
</template>

<script>
import axios from "axios";

export default {
  name: "Search",
  data: function() {
    return {
      terms: ""
    };
  },
  methods: {
    emitSearchEndedWithDelay: function(started, data) {
      // Add some artificial latency for very short requests to prevent the loading animation appearing too briefly.
      const latency = 500;
      const ended = Date.now();
      const elapsed = ended - started;
      let delay = 0;
      if (elapsed < latency) {
        delay = latency - elapsed;
      }
      setTimeout(() => this.$emit("search-ended", data), delay);
    },
    search: function() {
      const started = Date.now();
      this.$emit("search-started");
      axios
        .get(`/api/search/${this.terms}`)
        .then(response => {
          this.emitSearchEndedWithDelay(started, {
            success: true,
            entries: response.data
          });
        })
        .catch(response => {
          this.emitSearchEndedWithDelay(started, {
            success: false,
            error: response
          });
        });
    }
  }
};
</script>

<style scoped>
form {
  width: 100%;
}
input:focus {
  border-color: #33d8dd;
}
</style>

