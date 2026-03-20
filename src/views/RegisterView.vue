<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AuthForm from '@/components/auth/AuthForm.vue'
import { useAuthStore } from '@/stores/auth'
import { validateRegistration, type AuthFieldErrors } from '@/utils/authValidation'

type AuthFormExposed = {
  setErrors: (errors: AuthFieldErrors) => void
}

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<AuthFormExposed | null>(null)
const formError = ref('')
const redirectPath = computed(() => (typeof route.query.redirect === 'string' ? route.query.redirect : '/'))

const submitRegistration = (values: { name: string; email: string; password: string }) => {
  const errors = validateRegistration(values)
  formRef.value?.setErrors(errors)
  formError.value = ''

  if (Object.keys(errors).length > 0) {
    return
  }

  const result = authStore.register(values)
  if (!result.ok) {
    formError.value = result.error
    return
  }

  router.push(redirectPath.value)
}
</script>

<template>
  <AuthForm
    ref="formRef"
    mode="register"
    title="Create your account"
    description="Sign up to manage your tournaments and keep your progress saved on this device."
    submit-label="Register"
    :initial-error="formError"
    @submit="submitRegistration"
  >
    <p class="auth-link">
      Already have an account?
      <RouterLink :to="{ path: '/login', query: { redirect: redirectPath } }">Log in</RouterLink>
    </p>
  </AuthForm>
</template>

<style scoped>
.auth-link {
  margin-top: 1rem;
  color: var(--text-soft);
}

a {
  color: var(--accent-gold);
}
</style>
