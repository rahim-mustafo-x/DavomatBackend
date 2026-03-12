import { useState, useEffect } from 'react'
import { X, AlertCircle, ExternalLink } from 'lucide-react'
import { useAuthStore } from '../store/authStore'
import './PaymentBanner.css'

export default function PaymentBanner() {
  const [show, setShow] = useState(false)
  const [daysLeft, setDaysLeft] = useState(0)
  const user = useAuthStore((state) => state.user)

  useEffect(() => {
    if (!user || user.role === 'ROLE_ADMIN') {
      setShow(false)
      return
    }

    // Check if payment is expired or expiring soon
    if (!user.payedDate) {
      setShow(true)
      setDaysLeft(0)
      return
    }

    const payedDate = new Date(user.payedDate)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    payedDate.setHours(0, 0, 0, 0)

    const diffTime = payedDate - today
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

    if (diffDays <= 3) {
      setShow(true)
      setDaysLeft(diffDays)
    } else {
      setShow(false)
    }
  }, [user])

  const handleClose = () => {
    setShow(false)
  }

  const openTelegramBot = () => {
    window.open('https://t.me/davomatAppBot', '_blank')
  }

  if (!show) return null

  const isExpired = daysLeft <= 0
  const isUrgent = daysLeft <= 1 && daysLeft > 0

  return (
    <div className={`payment-banner ${isExpired ? 'expired' : isUrgent ? 'urgent' : 'warning'}`}>
      <div className="payment-banner-content">
        <div className="payment-banner-icon">
          <AlertCircle size={24} />
        </div>
        <div className="payment-banner-text">
          {isExpired ? (
            <>
              <strong>To'lov muddati tugagan!</strong>
              <span>Tizimdan foydalanishni davom ettirish uchun to'lovni yangilang.</span>
            </>
          ) : (
            <>
              <strong>To'lov muddati tugayapti!</strong>
              <span>{daysLeft} kun qoldi. Iltimos, to'lovni yangilang.</span>
            </>
          )}
        </div>
        <button className="payment-banner-btn" onClick={openTelegramBot}>
          <ExternalLink size={18} />
          To'lash
        </button>
        <button className="payment-banner-close" onClick={handleClose}>
          <X size={20} />
        </button>
      </div>
    </div>
  )
}
