<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AuthForm from '@/components/auth/AuthForm.vue'
import { useAuthStore } from '@/stores/auth'
import { validateLogin, type AuthFieldErrors } from '@/utils/authValidation'
import { useSocialAuth } from '@/composables/useSocialAuth'

type AuthFormExposed = {
  setErrors: (errors: AuthFieldErrors) => void
}

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<AuthFormExposed | null>(null)
const formError = ref('')
const { loadingProvider, signInWithGoogle } = useSocialAuth()

const submitLogin = async (values: { email: string; password: string; name: string }) => {
  const errors = validateLogin(values)
  formRef.value?.setErrors(errors)
  formError.value = ''

  if (Object.keys(errors).length > 0) {
    return
  }

  const result = await authStore.login(values)

  if (!result.ok) {
    formError.value = result.error
    return
  }

  router.push('/')
}

const loginWithGoogle = async () => {
  try {
    formError.value = ''
    const token = await signInWithGoogle()
    const result = await authStore.socialLogin({ provider: 'google', idToken: token })

    if (!result.ok) {
      formError.value = result.error
      return
    }

    router.push('/')
  } catch (error) {
    formError.value = error instanceof Error ? error.message : 'Google login failed.'
  }
}
</script>

<template>
  <AuthForm
    ref="formRef"
    mode="login"
    title="Welcome back"
    description="Log in to continue managing your tournaments."
    submit-label="Log in"
    :initial-error="formError"
    @submit="submitLogin"
  >
    <div class="social-divider">or</div>
    <div class="social-actions">
      <button type="button" class="secondary" :disabled="loadingProvider !== null" @click="loginWithGoogle">
        Continue with Google
      </button>
    </div>

    <p class="auth-link">
      Don't have an account?
      <RouterLink to="/register">Register</RouterLink>
    </p>
  </AuthForm>
</template>

<style scoped>
.social-divider {
  margin-top: 1rem;
  text-align: center;
  color: var(--text-soft);
}

.social-actions {
  margin-top: 0.75rem;
  display: grid;
  gap: 0.65rem;
}

.auth-link {
  margin-top: 1rem;
  color: var(--text-soft);
}

a {
  color: var(--accent-gold);
}
</style>
