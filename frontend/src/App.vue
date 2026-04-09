<script setup>
import { ref } from 'vue'
import { createShortUrl } from './api/urlShortener.js'
import { isLoggedIn, clearTokens } from './api/auth.js'
import AuthForm from './components/AuthForm.vue'
import UrlForm from './components/UrlForm.vue'
import ShortenedResult from './components/ShortenedResult.vue'
import ErrorMessage from './components/ErrorMessage.vue'

const authenticated = ref(isLoggedIn())
const result = ref(null)
const error = ref(null)
const loading = ref(false)

function onAuthenticated() {
  authenticated.value = true
}

function logout() {
  clearTokens()
  authenticated.value = false
  result.value = null
  error.value = null
}

async function handleSubmit(formData) {
  result.value = null
  error.value = null
  loading.value = true

  try {
    const data = await createShortUrl(formData)
    result.value = data
  } catch (err) {
    if (err.message === 'Session expired. Please log in again.') {
      authenticated.value = false
    }
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
      <div class="header-row">
        <div>
          <h1>URL Shortener</h1>
          <p class="subtitle">Paste a long URL and get a short, shareable link</p>
        </div>
        <button v-if="authenticated" class="btn-logout" @click="logout">Log out</button>
      </div>
    </header>

    <main>
      <AuthForm v-if="!authenticated" @authenticated="onAuthenticated" />

      <template v-else>
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
      </template>
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

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

.btn-logout {
  padding: 0.45rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: transparent;
  color: var(--color-text-secondary);
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  white-space: nowrap;
  margin-top: 0.35rem;
}

.btn-logout:hover {
  background: var(--color-error-bg);
  color: var(--color-error);
  border-color: var(--color-error);
}

.loading {
  margin-top: 1rem;
  color: var(--color-text-secondary);
  font-style: italic;
}
</style>
