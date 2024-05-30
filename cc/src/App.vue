<template>
    <navbar/>
    <router-view></router-view>
</template>

<script lang="ts" setup>
import navbar from './components/navbar.vue'
</script>

<style>
body {
  background-color: rgb(240, 244, 255);
}
</style>