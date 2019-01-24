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
      this.$emit("search-started");
      axios
        .get(`/api/search/${this.terms}`)
        .then(response => {
          this.$emit("search-ended", {
            success: true,
            entries: response.data
          });
        })
        .catch(response => {
          this.$emit("search-ended", {
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

