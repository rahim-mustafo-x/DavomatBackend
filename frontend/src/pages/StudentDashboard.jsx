import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import { 
  BookOpen, 
  Calendar, 
  DollarSign, 
  FileText, 
  LogOut,
  CheckCircle,
  XCircle,
  Clock
} from 'lucide-react'
import api from '../api/axios'
import { useAuthStore } from '../store/authStore'
import { ROLES } from '../constants/roles'
import './StudentDashboard.css'

export default function StudentDashboard() {
  const [activeSection, setActiveSection] = useState('courses')
  const [courses, setCourses] = useState([])
  const [attendance, setAttendance] = useState([])
  const [balance, setBalance] = useState(null)
  const [loading, setLoading] = useState(false)
  
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()

  // Verify role
  useEffect(() => {
    if (user?.role !== ROLES.STUDENT) {
      toast.error('Access denied')
      navigate('/')
    }
  }, [user, navigate])

  useEffect(() => {
    if (activeSection === 'courses') {
      loadCourses()
    } else if (activeSection === 'attendance') {
      loadAttendance()
    } else if (activeSection === 'balance') {
      loadBalance()
    }
  }, [activeSection])

  const loadCourses = async () => {
    setLoading(true)
    try {
      const response = await api.get('/api/student/seeCourses')
      setCourses(response.data.data || [])
    } catch (error) {
      toast.error('Failed to load courses')
    } finally {
      setLoading(false)
    }
  }

  const loadAttendance = async () => {
    setLoading(true)
    try {
      // This would need a student-specific endpoint
      // For now, showing placeholder
      setAttendance([])
    } catch (error) {
      toast.error('Failed to load attendance')
    } finally {
      setLoading(false)
    }
  }

  const loadBalance = async () => {
    setLoading(true)
    try {
      const response = await api.get('/api/student/balance')
      setBalance(response.data.data)
    } catch (error) {
      toast.error('Failed to load balance')
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
    toast.success('Logged out successfully')
  }

  const renderCourses = () => (
    <div className="section-content">
      <h2>My Courses</h2>
      
      {loading ? (
        <div className="loading-center">
          <div className="spinner"></div>
        </div>
      ) : courses.length === 0 ? (
        <div className="empty-state">
          <BookOpen size={60} />
          <p>You are not enrolled in any courses yet.</p>
        </div>
      ) : (
        <div className="courses-grid">
          {courses.map((course) => (
            <div key={course.courseId} className="course-card">
              <div className="course-icon">
                <BookOpen size={30} />
              </div>
              <h3>{course.courseName}</h3>
              <p className="course-group">Group: {course.groupName}</p>
              <button className="btn btn-primary btn-sm">View Details</button>
            </div>
          ))}
        </div>
      )}
    </div>
  )

  const renderAttendance = () => (
    <div className="section-content">
      <h2>Attendance Records</h2>
      
      {loading ? (
        <div className="loading-center">
          <div className="spinner"></div>
        </div>
      ) : attendance.length === 0 ? (
        <div className="empty-state">
          <Calendar size={60} />
          <p>No attendance records found.</p>
        </div>
      ) : (
        <div className="attendance-list">
          {attendance.map((record, index) => (
            <div key={index} className="attendance-item">
              <div className="attendance-date">
                <Calendar size={20} />
                {new Date(record.date).toLocaleDateString()}
              </div>
              <div className={`attendance-status ${record.status.toLowerCase()}`}>
                {record.status === 'PRESENT' ? (
                  <>
                    <CheckCircle size={20} />
                    Present
                  </>
                ) : record.status === 'ABSENT' ? (
                  <>
                    <XCircle size={20} />
                    Absent
                  </>
                ) : (
                  <>
                    <Clock size={20} />
                    Late
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )

  const renderBalance = () => (
    <div className="section-content">
      <h2>Payment Balance</h2>
      
      {loading ? (
        <div className="loading-center">
          <div className="spinner"></div>
        </div>
      ) : balance ? (
        <div className="balance-card">
          <div className="balance-icon">
            <DollarSign size={50} />
          </div>
          <div className="balance-info">
            <h3>Current Balance</h3>
            <p className="balance-amount">${balance.amount || 0}</p>
            <p className="balance-date">
              Valid until: {balance.limit ? new Date(balance.limit).toLocaleDateString() : 'N/A'}
            </p>
          </div>
          <button className="btn btn-primary">Make Payment</button>
        </div>
      ) : (
        <div className="empty-state">
          <DollarSign size={60} />
          <p>No balance information available.</p>
        </div>
      )}
    </div>
  )

  const renderAssignments = () => (
    <div className="section-content">
      <h2>Assignments</h2>
      <div className="empty-state">
        <FileText size={60} />
        <p>Assignment feature coming soon...</p>
      </div>
    </div>
  )

  return (
    <div className="student-dashboard">
      <aside className="sidebar">
        <div className="sidebar-header">
          <h2>📚 Student Panel</h2>
          <p>{user?.email}</p>
        </div>

        <nav className="sidebar-nav">
          <button
            className={`nav-link ${activeSection === 'courses' ? 'active' : ''}`}
            onClick={() => setActiveSection('courses')}
          >
            <BookOpen size={20} />
            My Courses
          </button>
          <button
            className={`nav-link ${activeSection === 'attendance' ? 'active' : ''}`}
            onClick={() => setActiveSection('attendance')}
          >
            <Calendar size={20} />
            Attendance
          </button>
          <button
            className={`nav-link ${activeSection === 'balance' ? 'active' : ''}`}
            onClick={() => setActiveSection('balance')}
          >
            <DollarSign size={20} />
            Balance
          </button>
          <button
            className={`nav-link ${activeSection === 'assignments' ? 'active' : ''}`}
            onClick={() => setActiveSection('assignments')}
          >
            <FileText size={20} />
            Assignments
          </button>
        </nav>

        <div className="sidebar-footer">
          <button onClick={handleLogout} className="btn btn-danger btn-full">
            <LogOut size={20} />
            Logout
          </button>
        </div>
      </aside>

      <main className="main-content">
        {activeSection === 'courses' && renderCourses()}
        {activeSection === 'attendance' && renderAttendance()}
        {activeSection === 'balance' && renderBalance()}
        {activeSection === 'assignments' && renderAssignments()}
      </main>
    </div>
  )
}
