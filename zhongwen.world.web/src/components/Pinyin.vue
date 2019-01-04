<template>
  <span>{{value}}</span>
</template>

<script>
export default {
  name: "Pinyin",
  props: ["value"]
};
</script>

<style scoped>
span {
  font-size: 0.75em;
  color: #666;
}
</style>

