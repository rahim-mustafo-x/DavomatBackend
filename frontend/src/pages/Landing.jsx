import { Link } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import { getDashboardPath } from '../constants/roles'
import { CheckCircle, BarChart3, Bell, Smartphone, LogIn, Moon, Sun, Mail, User, MessageSquare } from 'lucide-react'
import { useState, useEffect } from 'react'
import toast from 'react-hot-toast'
import api from '../api/axios'
import './Landing.css'

export default function Landing() {
  const { user } = useAuthStore()
  const [darkMode, setDarkMode] = useState(false)
  const [contactForm, setContactForm] = useState({
    name: '',
    email: '',
    message: ''
  })
  const [sending, setSending] = useState(false)

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

  const handleContactSubmit = async (e) => {
    e.preventDefault()
    
    if (!contactForm.name || !contactForm.email || !contactForm.message) {
      toast.error('Iltimos, barcha maydonlarni to\'ldiring')
      return
    }

    setSending(true)
    try {
      await api.post('/api/contact/contact', contactForm)
      toast.success('Xabaringiz yuborildi! Tez orada javob beramiz.')
      setContactForm({ name: '', email: '', message: '' })
    } catch (error) {
      toast.error('Xatolik yuz berdi. Iltimos, qayta urinib ko\'ring.')
    } finally {
      setSending(false)
    }
  }

  const handleTestEmail = async () => {
    try {
      const response = await api.get('/api/contact/test')
      toast.success(response.data)
    } catch (error) {
      toast.error('Test email yuborishda xatolik: ' + (error.response?.data || error.message))
    }
  }

  return (
    <div className="landing">
      <nav className="navbar">
        <div className="container">
          <div className="nav-content">
            <div className="logo">📊 Davomat App</div>
            <div className="nav-links">
              <a href="#features" className="nav-link">Features</a>
              <a href="#contact" className="nav-link">Contact</a>
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

      <section id="contact" className="contact-section">
        <div className="container">
          <h2 className="section-title">Biz bilan bog'laning</h2>
          <p className="section-subtitle">Savollaringiz bormi? Biz bilan bog'laning!</p>
          
          <div className="contact-content">
            <form onSubmit={handleContactSubmit} className="contact-form">
              <div className="form-group">
                <label>
                  <User size={20} />
                  Ismingiz
                </label>
                <input
                  type="text"
                  placeholder="Ismingizni kiriting"
                  value={contactForm.name}
                  onChange={(e) => setContactForm({ ...contactForm, name: e.target.value })}
                  required
                />
              </div>
              
              <div className="form-group">
                <label>
                  <Mail size={20} />
                  Email
                </label>
                <input
                  type="email"
                  placeholder="email@example.com"
                  value={contactForm.email}
                  onChange={(e) => setContactForm({ ...contactForm, email: e.target.value })}
                  required
                />
              </div>
              
              <div className="form-group">
                <label>
                  <MessageSquare size={20} />
                  Xabar
                </label>
                <textarea
                  rows="5"
                  placeholder="Xabaringizni yozing..."
                  value={contactForm.message}
                  onChange={(e) => setContactForm({ ...contactForm, message: e.target.value })}
                  required
                ></textarea>
              </div>
              
              <button type="submit" className="btn btn-primary btn-large" disabled={sending}>
                {sending ? 'Yuborilmoqda...' : 'Xabar yuborish'}
              </button>
            </form>
            
            <div className="contact-info">
              <div className="info-card">
                <Mail size={32} />
                <h3>Email</h3>
                <p>rahim.mustafo.x@gmail.com</p>
                <button 
                  onClick={handleTestEmail} 
                  className="btn btn-secondary"
                  style={{ marginTop: '16px', width: '100%' }}
                >
                  Test Email Yuborish
                </button>
              </div>
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
