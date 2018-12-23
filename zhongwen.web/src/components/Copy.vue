<template>
  <span>
    <a href class="uk-icon-link uk-align-right" uk-icon="copy"></a>
    <div uk-dropdown>
      <ul class="uk-nav uk-dropdown-nav">
        <li>
          <a href="#">{{entry.traditional}}</a>
        </li>
        <li>
          <a href="#">{{entry.simplified}}</a>
        </li>
        <li>
          <a href="#">{{entry.pinyin}}</a>
        </li>
        <li>
          <a href="#">{{entry.english}}</a>
        </li>
      </ul>
    </div>
  </span>
</template>

<script>
export default {
  name: "Copy",
  props: ["entry"]
};
</script>

<style scoped>
a {
  max-width: 100px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

