<template>
  <ol class="uk-list">
    <li v-for="(value, i) in values" :key="value">
      <span v-if="values.length > 1" class="uk-text-bold number">{{i + 1}}</span>
      <span class="value">{{value}}</span>
    </li>
  </ol>
</template>

<script>
export default {
  name: "English",
  props: ["values"]
};
</script>

<style scoped>
.number {
  margin-right: 10px;
}
</style>

