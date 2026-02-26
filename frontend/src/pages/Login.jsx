import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import { LogIn, Eye, EyeOff } from 'lucide-react'
import api from '../api/axios'
import { useAuthStore } from '../store/authStore'
import { getDashboardPath } from '../constants/roles'
import './Login.css'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const setAuth = useAuthStore((state) => state.setAuth)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)

    try {
      const response = await api.post('/auth/login', { 
        email: email.trim(), 
        password: password 
      })
      
      const { token, user, message } = response.data

      if (!token || !user) {
        throw new Error('Invalid response from server')
      }

      setAuth(user, token)
      toast.success(message || `Welcome back, ${user.firstName}!`)

      const dashboardPath = getDashboardPath(user.role)
      navigate(dashboardPath)
    } catch (error) {
      let errorMessage = 'Login failed. Please try again.'
      
      if (error.response) {
        if (error.response.status === 401 || error.response.status === 403) {
          errorMessage = 'Invalid email or password'
        } else if (error.response.data?.message) {
          errorMessage = error.response.data.message
        }
      } else if (error.request) {
        errorMessage = 'Cannot connect to server. Please check if backend is running.'
      }
      
      toast.error(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <div className="login-left">
        <div className="brand-section">
          <div className="brand-logo">
            <div className="logo-circle">
              <span className="logo-icon">📊</span>
            </div>
            <h1>Davomat</h1>
          </div>
          <p className="brand-tagline">Smart Attendance Management System</p>
        </div>

        <div className="features-showcase">
          <h3>Why Choose Davomat?</h3>
          <div className="feature-item">
            <div className="feature-icon-wrapper">
              <span className="feature-icon">⚡</span>
            </div>
            <div className="feature-text">
              <h4>Real-time Tracking</h4>
              <p>Monitor attendance instantly with live updates</p>
            </div>
          </div>
          <div className="feature-item">
            <div className="feature-icon-wrapper">
              <span className="feature-icon">📈</span>
            </div>
            <div className="feature-text">
              <h4>Advanced Analytics</h4>
              <p>Gain insights with powerful reporting tools</p>
            </div>
          </div>
          <div className="feature-item">
            <div className="feature-icon-wrapper">
              <span className="feature-icon">🔔</span>
            </div>
            <div className="feature-text">
              <h4>Instant Notifications</h4>
              <p>Stay informed with real-time alerts</p>
            </div>
          </div>
          <div className="feature-item">
            <div className="feature-icon-wrapper">
              <span className="feature-icon">🔒</span>
            </div>
            <div className="feature-text">
              <h4>Secure & Reliable</h4>
              <p>Enterprise-grade security for your data</p>
            </div>
          </div>
        </div>

        <div className="testimonial">
          <p>"Davomat has transformed how we manage attendance. It's intuitive, fast, and reliable."</p>
          <div className="testimonial-author">
            <strong>Sarah Johnson</strong>
            <span>School Administrator</span>
          </div>
        </div>
      </div>

      <div className="login-right">
        <div className="login-card">
          <div className="login-header">
            <h2>Welcome Back</h2>
            <p>Sign in to continue to your dashboard</p>
          </div>

          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="email">Email Address</label>
              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter your email"
                required
                autoComplete="email"
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <div className="password-input-wrapper">
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter your password"
                  required
                  autoComplete="current-password"
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                  tabIndex={-1}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
              </div>
            </div>

            <button type="submit" className="btn-login" disabled={loading}>
              {loading ? (
                <>
                  <span className="spinner-small"></span>
                  Signing in...
                </>
              ) : (
                <>
                  <LogIn size={20} />
                  Sign In
                </>
              )}
            </button>
          </form>

          <div className="login-footer">
            <p>Need help? <a href="mailto:support@davomat.uz">Contact Support</a></p>
          </div>
        </div>
      </div>
    </div>
  )
}
