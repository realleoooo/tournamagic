<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AuthForm from '@/components/auth/AuthForm.vue'
import { useAuthStore } from '@/stores/auth'
import { validateLogin, type AuthFieldErrors } from '@/utils/authValidation'

type AuthFormExposed = {
  setErrors: (errors: AuthFieldErrors) => void
}

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<AuthFormExposed | null>(null)
const formError = ref('')
const redirectPath = computed(() => (typeof route.query.redirect === 'string' ? route.query.redirect : '/'))

const submitLogin = (values: { email: string; password: string; name: string }) => {
  const errors = validateLogin(values)
  formRef.value?.setErrors(errors)
  formError.value = ''

  if (Object.keys(errors).length > 0) {
    return
  }

  const result = authStore.login(values)

  if (!result.ok) {
    formError.value = result.error
    return
  }

  router.push(redirectPath.value)
}
</script>

<template>
  <div class="auth-screen">
    <AuthForm
      ref="formRef"
      mode="login"
      title="Welcome back"
      description="Log in to continue managing your tournaments."
      submit-label="Log in"
      :initial-error="formError"
      @submit="submitLogin"
    >
      <p class="auth-link">
        Don't have an account?
        <RouterLink :to="{ path: '/register', query: { redirect: redirectPath } }">Register</RouterLink>
      </p>
    </AuthForm>
  </div>
</template>

<style scoped>
.auth-screen {
  min-height: 100%;
}

.auth-link {
  margin-top: 1.25rem;
  text-align: center;
  color: #e7cea0;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

a {
  color: #f4d27d;
}
</style>
