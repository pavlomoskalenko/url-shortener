<script setup>
import { ref } from 'vue'
import { createShortUrl } from './api/urlShortener.js'
import UrlForm from './components/UrlForm.vue'
import ShortenedResult from './components/ShortenedResult.vue'
import ErrorMessage from './components/ErrorMessage.vue'

const result = ref(null)
const error = ref(null)
const loading = ref(false)

async function handleSubmit(formData) {
  result.value = null
  error.value = null
  loading.value = true

  try {
    const data = await createShortUrl(formData)
    result.value = data
  } catch (err) {
    error.value = {
      message: err.message,
      fieldErrors: err.fieldErrors,
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="container">
    <header>
      <h1>URL Shortener</h1>
      <p class="subtitle">Paste a long URL and get a short, shareable link</p>
    </header>

    <main>
      <UrlForm @submit="handleSubmit" />

      <p v-if="loading" class="loading">Shortening...</p>

      <ShortenedResult
        v-if="result"
        :short-url="result.shortUrl"
        :expiration-date="result.expirationDate"
      />

      <ErrorMessage
        v-if="error"
        :message="error.message"
        :field-errors="error.fieldErrors"
      />
    </main>
  </div>
</template>

<style scoped>
.container {
  width: 100%;
  max-width: 560px;
  margin: 0 auto;
}

header {
  margin-bottom: 2rem;
}

header h1 {
  margin: 0;
  font-size: 2rem;
}

.subtitle {
  margin: 0.35rem 0 0;
  color: var(--color-text-secondary);
  font-size: 0.95rem;
}

.loading {
  margin-top: 1rem;
  color: var(--color-text-secondary);
  font-style: italic;
}
</style>
