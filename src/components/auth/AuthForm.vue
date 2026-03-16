<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { AuthFieldErrors } from '@/utils/authValidation'

type AuthFormMode = 'register' | 'login'

type AuthSubmitValues = {
  name: string
  email: string
  password: string
}

const props = defineProps<{
  mode: AuthFormMode
  submitLabel: string
  title: string
  description: string
  initialError?: string
  loading?: boolean
}>()

const emit = defineEmits<{
  submit: [values: AuthSubmitValues]
}>()

const form = reactive<AuthSubmitValues>({
  name: '',
  email: '',
  password: ''
})

const fieldErrors = ref<AuthFieldErrors>({})

const setErrors = (errors: AuthFieldErrors) => {
  fieldErrors.value = errors
}

const clearError = (field: keyof AuthFieldErrors) => {
  if (!fieldErrors.value[field]) {
    return
  }

  fieldErrors.value = {
    ...fieldErrors.value,
    [field]: undefined
  }
}

const handleSubmit = () => {
  emit('submit', {
    name: form.name,
    email: form.email,
    password: form.password
  })
}

defineExpose({
  setErrors
})
</script>

<template>
  <section class="card auth-card">
    <h2>{{ title }}</h2>
    <p class="auth-card__description">{{ description }}</p>

    <p v-if="initialError" class="auth-card__error">{{ initialError }}</p>

    <form class="auth-form" @submit.prevent="handleSubmit" novalidate>
      <label v-if="mode === 'register'" class="auth-form__field">
        Name
        <input
          v-model="form.name"
          name="name"
          type="text"
          autocomplete="name"
          @input="clearError('name')"
        />
        <span v-if="fieldErrors.name" class="auth-form__error">{{ fieldErrors.name }}</span>
      </label>

      <label class="auth-form__field">
        Email
        <input
          v-model="form.email"
          name="email"
          type="email"
          autocomplete="email"
          @input="clearError('email')"
        />
        <span v-if="fieldErrors.email" class="auth-form__error">{{ fieldErrors.email }}</span>
      </label>

      <label class="auth-form__field">
        Password
        <input
          v-model="form.password"
          name="password"
          type="password"
          autocomplete="current-password"
          @input="clearError('password')"
        />
        <span v-if="fieldErrors.password" class="auth-form__error">{{ fieldErrors.password }}</span>
      </label>

      <button type="submit" :disabled="loading">{{ submitLabel }}</button>
    </form>

    <slot />
  </section>
</template>

<style scoped>
.auth-card {
  margin: 0 auto;
  max-width: 480px;
}

.auth-card__description {
  color: var(--text-soft);
}

.auth-card__error {
  border: 1px solid color-mix(in srgb, var(--danger) 50%, transparent);
  border-radius: 8px;
  padding: 0.65rem;
  color: #fecaca;
  background: color-mix(in srgb, var(--danger) 20%, transparent);
}

.auth-form {
  display: grid;
  gap: 0.85rem;
}

.auth-form__field {
  display: grid;
  gap: 0.45rem;
  font-size: 0.95rem;
  font-weight: 600;
}

.auth-form__error {
  color: #fca5a5;
  font-size: 0.85rem;
}
</style>
