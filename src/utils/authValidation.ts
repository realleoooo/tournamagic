export type AuthFieldErrors = {
  name?: string
  email?: string
  password?: string
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const MIN_PASSWORD_LENGTH = 8

export const validateEmail = (email: string): string | null => {
  if (!email.trim()) {
    return 'Email is required.'
  }

  if (!EMAIL_REGEX.test(email.trim())) {
    return 'Please enter a valid email address.'
  }

  return null
}

export const validatePassword = (password: string): string | null => {
  if (!password) {
    return 'Password is required.'
  }

  if (password.length < MIN_PASSWORD_LENGTH) {
    return `Password must be at least ${MIN_PASSWORD_LENGTH} characters.`
  }

  return null
}

export const validateName = (name: string): string | null => {
  if (!name.trim()) {
    return 'Name is required.'
  }

  return null
}

export const validateRegistration = (values: {
  name: string
  email: string
  password: string
}): AuthFieldErrors => {
  const errors: AuthFieldErrors = {}
  const nameError = validateName(values.name)
  const emailError = validateEmail(values.email)
  const passwordError = validatePassword(values.password)

  if (nameError) {
    errors.name = nameError
  }

  if (emailError) {
    errors.email = emailError
  }

  if (passwordError) {
    errors.password = passwordError
  }

  return errors
}

export const validateLogin = (values: { email: string; password: string }): AuthFieldErrors => {
  const errors: AuthFieldErrors = {}
  const emailError = validateEmail(values.email)

  if (emailError) {
    errors.email = emailError
  }

  if (!values.password) {
    errors.password = 'Password is required.'
  }

  return errors
}
