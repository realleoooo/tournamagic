import { describe, expect, it } from 'vitest'
import { validateLogin, validateRegistration } from '../src/utils/authValidation'

describe('authValidation', () => {
  it('validates registration required fields and password length', () => {
    const errors = validateRegistration({
      name: '',
      email: 'bad-email',
      password: '123'
    })

    expect(errors).toEqual({
      name: 'Name is required.',
      email: 'Please enter a valid email address.',
      password: 'Password must be at least 8 characters.'
    })
  })

  it('returns no registration errors for valid data', () => {
    const errors = validateRegistration({
      name: 'Jace Beleren',
      email: 'jace@example.com',
      password: 'mindsculptor'
    })

    expect(errors).toEqual({})
  })

  it('validates login required fields', () => {
    const errors = validateLogin({
      email: '',
      password: ''
    })

    expect(errors).toEqual({
      email: 'Email is required.',
      password: 'Password is required.'
    })
  })
})
