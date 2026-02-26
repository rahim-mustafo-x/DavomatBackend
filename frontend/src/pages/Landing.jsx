import { Link } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import { getDashboardPath } from '../constants/roles'
import { CheckCircle, BarChart3, Bell, Smartphone, LogIn, Moon, Sun } from 'lucide-react'
import { useState, useEffect } from 'react'
import './Landing.css'

export default function Landing() {
  const { user } = useAuthStore()
  const [darkMode, setDarkMode] = useState(false)

  useEffect(() => {
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme === 'dark') {
      setDarkMode(true)
      document.body.classList.add('dark-mode')
    }
  }, [])

  const toggleTheme = () => {
    setDarkMode(!darkMode)
    if (!darkMode) {
      document.body.classList.add('dark-mode')
      localStorage.setItem('theme', 'dark')
    } else {
      document.body.classList.remove('dark-mode')
      localStorage.setItem('theme', 'light')
    }
  }

  return (
    <div className="landing">
      <nav className="navbar">
        <div className="container">
          <div className="nav-content">
            <div className="logo">📊 Davomat App</div>
            <div className="nav-links">
              <button onClick={toggleTheme} className="theme-toggle">
                {darkMode ? <Sun size={20} /> : <Moon size={20} />}
              </button>
              {user ? (
                <Link to={getDashboardPath(user.role)} className="btn btn-primary">
                  Dashboard
                </Link>
              ) : (
                <Link to="/login" className="btn btn-primary">
                  <LogIn size={20} />
                  Sign In
                </Link>
              )}
            </div>
          </div>
        </div>
      </nav>

      <section className="hero">
        <div className="container">
          <div className="hero-content">
            <h1 className="hero-title">Modern Attendance Management System</h1>
            <p className="hero-subtitle">
              Track attendance, manage courses, and analyze data with our powerful and intuitive platform
            </p>
            <div className="hero-buttons">
              <Link to="/login" className="btn btn-primary btn-large">
                Get Started
              </Link>
              <a href="#features" className="btn btn-secondary btn-large">
                Learn More
              </a>
            </div>
          </div>
        </div>
      </section>

      <section id="features" className="features">
        <div className="container">
          <h2 className="section-title">Powerful Features</h2>
          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">
                <CheckCircle size={40} />
              </div>
              <h3>Real-time Tracking</h3>
              <p>Track attendance in real-time with instant updates and notifications</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">
                <BarChart3 size={40} />
              </div>
              <h3>Advanced Analytics</h3>
              <p>Comprehensive analytics and reporting for better insights</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">
                <Bell size={40} />
              </div>
              <h3>Instant Notifications</h3>
              <p>Get notified instantly about important events and updates</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">
                <Smartphone size={40} />
              </div>
              <h3>Mobile Friendly</h3>
              <p>Access from any device with our responsive design</p>
            </div>
          </div>
        </div>
      </section>

      <section className="cta">
        <div className="container">
          <h2>Ready to get started?</h2>
          <p>Join thousands of institutions using Davomat App</p>
          <Link to="/login" className="btn btn-primary btn-large">
            Sign In Now
          </Link>
        </div>
      </section>

      <footer className="footer">
        <div className="container">
          <p>&copy; 2026 Davomat App. All rights reserved.</p>
        </div>
      </footer>
    </div>
  )
}
