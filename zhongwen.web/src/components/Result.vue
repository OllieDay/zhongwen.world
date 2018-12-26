<template>
  <li>
    <h3>
      <Simplified :value="entry.simplified" :tones="entry.tones" class="search-result-value"/>
      <span class="uk-text-muted separator">|</span>
      <Traditional :value="entry.traditional" :tones="entry.tones"/>
      <Pinyin :value="entry.pinyin" class="pinyin"/>
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
  .separator {
    margin: 0 10px;
  }
  .pinyin {
    margin-left: 10px;
  }
</style>

