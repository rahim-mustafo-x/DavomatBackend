import { AlertCircle, ExternalLink, LogOut } from 'lucide-react'
import { useAuthStore } from '../store/authStore'
import { useNavigate } from 'react-router-dom'
import './PaymentExpiredScreen.css'

export default function PaymentExpiredScreen() {
  const logout = useAuthStore((state) => state.logout)
  const navigate = useNavigate()

  const openTelegramBot = () => {
    window.open('https://t.me/davomatAppBot', '_blank')
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="payment-expired-screen">
      <div className="payment-expired-content">
        <div className="payment-expired-icon">
          <AlertCircle size={80} />
        </div>
        <h1>To'lov Muddati Tugagan</h1>
        <p className="main-message">
          Sizning obuna muddatingiz tugagan. Tizimdan foydalanishni davom ettirish uchun 
          to'lovni yangilang.
        </p>
        <p className="sub-message">
          Your subscription has expired. Please renew your payment to continue using the system.
        </p>
        
        <div className="payment-actions">
          <button className="btn-pay" onClick={openTelegramBot}>
            <ExternalLink size={20} />
            Telegram Bot orqali to'lash
          </button>
          <button className="btn-logout-secondary" onClick={handleLogout}>
            <LogOut size={20} />
            Chiqish
          </button>
        </div>

        <div className="telegram-info-box">
          <p>📱 Telegram Bot: <strong>@davomatAppBot</strong></p>
          <p className="info-text">
            Botga kirib, to'lovni amalga oshiring. To'lov amalga oshirilgandan so'ng 
            tizimga qayta kiring.
          </p>
        </div>
      </div>
    </div>
  )
}
