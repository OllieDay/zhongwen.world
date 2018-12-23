<template>
  <li>
    <h3>
      <Simplified :value="entry.simplified" class="search-result-value"/>
      <Traditional :value="entry.traditional" class="search-result-value"/>
      <Pinyin :value="entry.pinyin"/>
      <Copy :entry="entry"/>
    </h3>
    <English :values="entry.english"/>
  </li>
</template>

<script>
import Traditional from "./Traditional.vue";
import Simplified from "./Simplified.vue";
import Pinyin from "./Pinyin.vue";
import English from "./English.vue";
import Copy from "./Copy.vue";

export default {
  name: "Result",
  components: {
    Traditional,
    Simplified,
    Pinyin,
    English,
    Copy
  },
  props: ["entry"]
};
</script>

<style scoped>
  .search-result-value {
    margin-right: 10px;
  }
</style>

