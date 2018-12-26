<template>
  <span>
    <Character
      v-for="(character, i) in value.split('')"
      :key="character"
      :character="character"
      :tone="tones[i]"
    ></Character>
  </span>
</template>

<script>
import Character from "./Character.vue";

export default {
  name: "Simplified",
  components: {
    Character
  },
  props: ["value", "tones"]
};
</script>
