<template>
  <div class="uk-container uk-container-small uk-margin-medium">
    <Search v-on:search-started="onSearchStarted" v-on:search-ended="onSearchEnded"/>
    <ul class="uk-list uk-list-divider"></ul>
    <div v-if="loading" class="spinner" uk-spinner="ratio: 2"></div>
    <Results v-else-if="entries.length > 0" :entries="entries"/>
    <NoResults v-else-if="loaded"/>
  </div>
</template>

<script>
import Search from "./Search.vue";
import Results from "./Results.vue";
import NoResults from "./NoResults.vue";

export default {
  name: "Content",
  components: {
    Search,
    Results,
    NoResults
  },
  data: function() {
    return {
      // Set to true after every search and is used to prevent showing the "No results" message on first load.
      loaded: false,
      loading: false,
      entries: []
    };
  },
  methods: {
    onSearchStarted: function() {
      this.loading = true;
    },
    onSearchEnded: function(result) {
      this.loaded = true;
      this.loading = false;
      if (result.success) {
        this.entries = result.entries;
      } else {
        // TODO: error handling
      }
    }
  }
};
</script>

<style scoped>
.spinner {
  width: 100%;
  text-align: center;
  color: #33d8dd;
}
</style>
