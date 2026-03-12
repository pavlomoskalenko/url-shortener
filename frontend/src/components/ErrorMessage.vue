<script setup>
defineProps({
  message: { type: String, required: true },
  fieldErrors: { type: Object, default: null },
})
</script>

<template>
  <div class="error-box">
    <p class="error-message">{{ message }}</p>
    <ul v-if="fieldErrors" class="field-errors">
      <li v-for="(msg, field) in fieldErrors" :key="field">
        <strong>{{ field }}:</strong> {{ msg }}
      </li>
    </ul>
  </div>
</template>

<style scoped>
.error-box {
  margin-top: 1rem;
  padding: 0.85rem 1rem;
  background: var(--color-error-bg);
  border: 1px solid var(--color-error);
  border-radius: 8px;
  color: var(--color-error);
}

.error-message {
  margin: 0;
  font-weight: 600;
  font-size: 0.9rem;
}

.field-errors {
  margin: 0.5rem 0 0;
  padding-left: 1.2rem;
  font-size: 0.85rem;
}

.field-errors li {
  margin-bottom: 0.2rem;
}
</style>
