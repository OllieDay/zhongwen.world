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
    search: function() {
      const started = Date.now();
      this.$emit("search-started");
      axios
        .get(`http://localhost/api/search/${this.terms}`)
        .then(response => {
          const ended = Date.now();
          const delay = ended - started;
          setTimeout(() => {
            this.$emit("search-ended", {
              success: true,
              entries: response.data
            });
          }, 500);
        })
        .catch(response => {
          const ended = Date.now();
          const delay = ended - started;
          setTimeout(() => {
            this.$emit("search-ended", {
              success: false,
              error: response
            });
          }, 500);
        });
    }
  }
};
</script>

<style scoped>
form {
  width: 100%;
}
</style>

