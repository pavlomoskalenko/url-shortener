<script setup>
import { reactive, ref } from 'vue'

const emit = defineEmits(['authenticated'])

const mode = ref('login')
const form = reactive({ username: '', password: '' })
const error = ref(null)
const fieldErrors = ref(null)
const loading = ref(false)

import { login, register } from '../api/auth.js'

async function handleSubmit() {
  error.value = null
  fieldErrors.value = null
  loading.value = true

  try {
    if (mode.value === 'login') {
      await login(form.username, form.password)
      emit('authenticated')
    } else {
      await register(form.username, form.password)
      await login(form.username, form.password)
      emit('authenticated')
    }
  } catch (err) {
    error.value = err.message
    fieldErrors.value = err.fieldErrors || null
  } finally {
    loading.value = false
  }
}

function toggleMode() {
  mode.value = mode.value === 'login' ? 'register' : 'login'
  error.value = null
  fieldErrors.value = null
}
</script>

<template>
  <div class="auth-form">
    <h2>{{ mode === 'login' ? 'Log In' : 'Register' }}</h2>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="username">Username</label>
        <input
          id="username"
          v-model="form.username"
          type="text"
          placeholder="Enter username"
          required
        />
      </div>

      <div class="form-group">
        <label for="password">Password</label>
        <input
          id="password"
          v-model="form.password"
          type="password"
          placeholder="Enter password"
          required
        />
      </div>

      <button type="submit" class="btn-auth" :disabled="loading">
        {{ loading ? 'Please wait...' : (mode === 'login' ? 'Log In' : 'Register') }}
      </button>
    </form>

    <div v-if="error" class="error-box">
      <p class="error-message">{{ error }}</p>
      <ul v-if="fieldErrors" class="field-errors">
        <li v-for="(msg, field) in fieldErrors" :key="field">
          <strong>{{ field }}:</strong> {{ msg }}
        </li>
      </ul>
    </div>

    <p class="toggle-link">
      {{ mode === 'login' ? "Don't have an account?" : 'Already have an account?' }}
      <a href="#" @click.prevent="toggleMode">
        {{ mode === 'login' ? 'Register' : 'Log In' }}
      </a>
    </p>
  </div>
</template>

<style scoped>
.auth-form {
  width: 100%;
}

.auth-form h2 {
  margin: 0 0 1.25rem;
  font-size: 1.5rem;
}

.auth-form form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
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

.btn-auth {
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

.btn-auth:hover {
  background: var(--color-primary-hover);
}

.btn-auth:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

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

.toggle-link {
  margin-top: 1rem;
  font-size: 0.9rem;
  color: var(--color-text-secondary);
  text-align: center;
}

.toggle-link a {
  color: var(--color-primary);
  font-weight: 600;
}
</style>
