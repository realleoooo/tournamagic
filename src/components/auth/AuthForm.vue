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
  <section class="auth-card-shell">
    <section class="auth-card">
      <h2>{{ title }}</h2>
      <p class="auth-card__description">{{ description }}</p>

      <p v-if="initialError" class="auth-card__error">{{ initialError }}</p>

      <form class="auth-form" @submit.prevent="handleSubmit" novalidate>
        <label v-if="mode === 'register'" class="auth-form__field">
          <span>Name</span>
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
          <span>Email</span>
          <input
            v-model="form.email"
            name="email"
            type="email"
            autocomplete="email"
            @input="clearError('email')"
          />
          <span v-if="fieldErrors.email" class="auth-form__error">{{ fieldErrors.email }}</span>
        </label>

        <label class="auth-form__field auth-form__field--password">
          <span>Password</span>
          <div class="auth-form__input-wrap">
            <span class="auth-form__icon" aria-hidden="true">🔒</span>
            <input
              v-model="form.password"
              name="password"
              type="password"
              autocomplete="current-password"
              @input="clearError('password')"
            />
          </div>
          <span v-if="fieldErrors.password" class="auth-form__error">{{ fieldErrors.password }}</span>
        </label>

        <button type="submit" class="auth-form__submit" :disabled="loading">{{ submitLabel }}</button>
      </form>

      <slot />
    </section>
  </section>
</template>

<style scoped>
.auth-card-shell {
  min-height: calc(100vh - 170px);
  display: grid;
  place-items: center;
  padding: 1rem;
}

.auth-card {
  position: relative;
  width: min(100%, 640px);
  background:
    linear-gradient(180deg, rgba(92, 55, 33, 0.24), rgba(33, 20, 14, 0.18)),
    rgba(66, 40, 26, 0.26);
  border: 1px solid #8f6030;
  box-shadow:
    inset 0 0 0 1px rgba(237, 190, 105, 0.16),
    0 20px 34px rgba(0, 0, 0, 0.26);
  padding: 3.4rem 4rem 3rem;
}

.auth-card::before,
.auth-card::after {
  content: '';
  position: absolute;
  left: 50%;
  width: 22px;
  height: 22px;
  margin-left: -11px;
  transform: rotate(45deg);
  border: 1px solid rgba(223, 177, 95, 0.78);
  background: linear-gradient(135deg, rgba(255, 229, 163, 0.9), rgba(153, 91, 30, 0.45));
  box-shadow: 0 0 12px rgba(255, 187, 72, 0.26);
}

.auth-card::before {
  top: -11px;
}

.auth-card::after {
  bottom: -11px;
}

.auth-card h2 {
  margin: 0;
  text-align: center;
  color: #f5dfb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 2.15rem;
}

.auth-card__description {
  margin: 0.8rem 0 0;
  text-align: center;
  color: #e6cc9d;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

.auth-card__error {
  margin: 1.2rem 0 0;
  border: 1px solid rgba(177, 100, 70, 0.72);
  background:
    linear-gradient(180deg, rgba(92, 46, 35, 0.92), rgba(51, 22, 17, 0.98)),
    #4a2119;
  color: #f4c1a7;
  padding: 0.75rem 0.95rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.auth-form {
  margin-top: 1.6rem;
  display: grid;
  gap: 1.2rem;
}

.auth-form__field {
  display: grid;
  gap: 0.55rem;
}

.auth-form__field > span:first-child {
  color: #f3ddb1;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
  font-weight: 700;
}

.auth-form input {
  background:
    linear-gradient(180deg, rgba(23, 16, 12, 0.96), rgba(19, 14, 11, 0.98)),
    #18120e;
  border-color: rgba(174, 128, 69, 0.84);
  color: #f2ddb6;
  border-radius: 12px;
  padding: 0.92rem 1rem;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.36);
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

.auth-form__input-wrap {
  position: relative;
}

.auth-form__field--password input {
  padding-left: 3rem;
}

.auth-form__icon {
  position: absolute;
  left: 0.9rem;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0.72;
  font-size: 1rem;
  pointer-events: none;
}

.auth-form__submit {
  margin-top: 0.2rem;
  background:
    linear-gradient(180deg, rgba(130, 170, 78, 0.96), rgba(59, 98, 36, 0.98)),
    #618f43;
  border-color: rgba(211, 173, 103, 0.8);
  color: #faf2d6;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.15rem;
  padding-block: 0.82rem;
  box-shadow:
    inset 0 1px 0 rgba(230, 255, 198, 0.24),
    0 0 0 1px rgba(69, 45, 22, 0.42);
}

.auth-form__submit:hover {
  background:
    linear-gradient(180deg, rgba(143, 187, 87, 0.96), rgba(69, 110, 44, 0.98)),
    #6e9d4d;
}

.auth-form__error {
  color: #f1b39a;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.88rem;
}

@media (max-width: 720px) {
  .auth-card {
    padding: 2.7rem 1.35rem 2.35rem;
  }
}
</style>
