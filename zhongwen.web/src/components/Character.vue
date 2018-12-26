<template>
  <span :class="toneClass">{{character}}</span>
</template>

<script>
export default {
  name: "Character",
  props: ["character", "tone"],
  computed: {
    toneClass: function() {
      return `tone-${this.tone}`;
    }
  }
};
</script>

<style scoped>
.tone-1 {
  color: #ef5350;
}
.tone-2 {
  color: #66bb6a;
}
.tone-3 {
  color: #42a5f5;
}
.tone-4 {
  color: #7e57c2;
}
.tone-5 {
  color: #9e9e9e;
}
</style>

