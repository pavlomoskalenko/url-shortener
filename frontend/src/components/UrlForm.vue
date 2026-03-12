<script setup>
import { reactive } from 'vue'

const emit = defineEmits(['submit'])

const form = reactive({
  originalUrl: '',
  customAlias: '',
  expireInDays: '',
})

function handleSubmit() {
  emit('submit', { ...form })
}
</script>

<template>
  <form class="url-form" @submit.prevent="handleSubmit">
    <div class="form-group">
      <label for="originalUrl">URL to shorten *</label>
      <input
        id="originalUrl"
        v-model="form.originalUrl"
        type="url"
        placeholder="https://example.com/very/long/path"
        required
      />
    </div>

    <div class="form-row">
      <div class="form-group">
        <label for="customAlias">Custom alias</label>
        <input
          id="customAlias"
          v-model="form.customAlias"
          type="text"
          placeholder="my-link"
        />
      </div>

      <div class="form-group">
        <label for="expireInDays">Expire in (days)</label>
        <input
          id="expireInDays"
          v-model="form.expireInDays"
          type="number"
          min="1"
          max="365"
          placeholder="30"
        />
      </div>
    </div>

    <button type="submit" class="btn-shorten">Shorten URL</button>
  </form>
</template>

<style scoped>
.url-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  flex: 1;
}

.form-group label {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--color-text-secondary);
}

.form-group input {
  padding: 0.65rem 0.85rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.95rem;
  background: var(--color-input-bg);
  color: var(--color-text);
  transition: border-color 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--color-primary-glow);
}

.form-row {
  display: flex;
  gap: 1rem;
}

.btn-shorten {
  padding: 0.75rem 1.5rem;
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-shorten:hover {
  background: var(--color-primary-hover);
}

@media (max-width: 500px) {
  .form-row {
    flex-direction: column;
  }
}
</style>
