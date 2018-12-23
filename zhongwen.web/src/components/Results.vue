<template>
  <ul class="uk-list uk-list-divider">
    <Result v-for="entry in entries" :key="entry.traditional" :entry="entry"/>
  </ul>
</template>

<script>
import Result from "./Result.vue";

export default {
  name: "Results",
  components: {
    Result
  },
  props: ['entries']
};
</script>
