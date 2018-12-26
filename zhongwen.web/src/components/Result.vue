<template>
  <li>
    <h3>
      <Chinese :value="entry.simplified" :tones="entry.tones"/>
      <span class="uk-text-muted separator">|</span>
      <Chinese :value="entry.traditional" :tones="entry.tones"/>
      <Pinyin :value="entry.pinyin" class="pinyin"/>
    </h3>
    <English :values="entry.english"/>
  </li>
</template>

<script>
import Chinese from "./Chinese.vue";
import Pinyin from "./Pinyin.vue";
import English from "./English.vue";

export default {
  name: "Result",
  components: {
    Chinese,
    Pinyin,
    English
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

