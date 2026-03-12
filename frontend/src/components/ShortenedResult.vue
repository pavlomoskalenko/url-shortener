<script setup>
import { ref } from 'vue'

const props = defineProps({
  shortUrl: { type: String, required: true },
  expirationDate: { type: String, default: null },
})

const copied = ref(false)

async function copyToClipboard() {
  try {
    await navigator.clipboard.writeText(props.shortUrl)
    copied.value = true
    setTimeout(() => (copied.value = false), 2000)
  } catch {
    /* clipboard API may be unavailable */
  }
}

function formatDate(iso) {
  if (!iso) return null
  const d = new Date(iso)
  return d.toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}
</script>

<template>
  <div class="result">
    <p class="result-label">Your shortened URL</p>
    <div class="result-row">
      <a :href="shortUrl" target="_blank" class="short-link">{{ shortUrl }}</a>
      <button class="btn-copy" @click="copyToClipboard">
        {{ copied ? 'Copied!' : 'Copy' }}
      </button>
    </div>
    <p v-if="expirationDate" class="expiration">
      Expires on {{ formatDate(expirationDate) }}
    </p>
  </div>
</template>

<style scoped>
.result {
  margin-top: 1.5rem;
  padding: 1.25rem;
  background: var(--color-result-bg);
  border: 1px solid var(--color-primary);
  border-radius: 8px;
}

.result-label {
  margin: 0 0 0.5rem;
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--color-text-secondary);
}

.result-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.short-link {
  font-size: 1.15rem;
  font-weight: 600;
  word-break: break-all;
  color: var(--color-primary);
}

.btn-copy {
  flex-shrink: 0;
  padding: 0.4rem 0.85rem;
  border: 1px solid var(--color-primary);
  border-radius: 5px;
  background: transparent;
  color: var(--color-primary);
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.btn-copy:hover {
  background: var(--color-primary);
  color: #fff;
}

.expiration {
  margin: 0.6rem 0 0;
  font-size: 0.8rem;
  color: var(--color-text-secondary);
}
</style>
