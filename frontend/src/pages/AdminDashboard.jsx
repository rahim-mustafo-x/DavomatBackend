import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import { 
  LayoutDashboard, 
  BarChart3, 
  FileText, 
  Activity, 
  Users, 
  Settings, 
  LogOut,
  RefreshCw,
  Download,
  Trash2,
  ChevronLeft,
  ChevronRight
} from 'lucide-react'
import api from '../api/axios'
import { useAuthStore } from '../store/authStore'
import './AdminDashboard.css'

export default function AdminDashboard() {
  const [activeSection, setActiveSection] = useState('dashboard')
  const [stats, setStats] = useState(null)
  const [logs, setLogs] = useState([])
  const [activity, setActivity] = useState(null)
  const [performance, setPerformance] = useState(null)
  const [loading, setLoading] = useState(false)
  const [logsPage, setLogsPage] = useState(0)
  const [logsTotalPages, setLogsTotalPages] = useState(0)
  const [selectedLogs, setSelectedLogs] = useState([])
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()

  useEffect(() => {
    loadDashboardStats()
    loadRecentActivity()
  }, [])

  useEffect(() => {
    if (activeSection === 'logs') {
      loadLogs()
    } else if (activeSection === 'performance') {
      loadPerformance()
    }
  }, [activeSection, logsPage])

  const loadDashboardStats = async () => {
    try {
      const response = await api.get('/api/statistics/dashboard')
      setStats(response.data.data)
    } catch (error) {
      toast.error('Failed to load statistics')
    }
  }

  const loadRecentActivity = async () => {
    try {
      const response = await api.get('/api/statistics/activity')
      setActivity(response.data.data)
    } catch (error) {
      console.error('Failed to load activity')
    }
  }

  const loadPerformance = async () => {
    setLoading(true)
    try {
      const response = await api.get('/api/statistics/performance')
      setPerformance(response.data.data)
    } catch (error) {
      toast.error('Failed to load performance metrics')
    } finally {
      setLoading(false)
    }
  }

  const loadLogs = async () => {
    setLoading(true)
    try {
      const response = await api.get(`/api/system-logs?page=${logsPage}&size=20`)
      setLogs(response.data.content || [])
      setLogsTotalPages(response.data.totalPages || 0)
    } catch (error) {
      toast.error('Failed to load logs')
    } finally {
      setLoading(false)
    }
  }

  const handleDeleteLog = async (id) => {
    if (!window.confirm('Are you sure you want to delete this log?')) return
    
    try {
      await api.delete(`/api/system-logs/${id}`)
      toast.success('Log deleted successfully')
      loadLogs()
    } catch (error) {
      toast.error('Failed to delete log')
    }
  }

  const handleDeleteSelected = async () => {
    if (selectedLogs.length === 0) {
      toast.error('No logs selected')
      return
    }
    
    if (!window.confirm(`Are you sure you want to delete ${selectedLogs.length} log(s)?`)) return
    
    try {
      await api.delete('/api/system-logs/bulk', { data: selectedLogs })
      toast.success(`${selectedLogs.length} log(s) deleted successfully`)
      setSelectedLogs([])
      loadLogs()
    } catch (error) {
      toast.error('Failed to delete logs')
    }
  }

  const handleDeleteAll = async () => {
    if (!window.confirm('⚠️ Are you sure you want to delete ALL logs? This action cannot be undone!')) return
    
    try {
      await api.delete('/api/system-logs/all')
      toast.success('All logs deleted successfully')
      setSelectedLogs([])
      loadLogs()
    } catch (error) {
      toast.error('Failed to delete all logs')
    }
  }

  const toggleLogSelection = (id) => {
    setSelectedLogs(prev => 
      prev.includes(id) ? prev.filter(logId => logId !== id) : [...prev, id]
    )
  }

  const toggleSelectAll = () => {
    if (selectedLogs.length === logs.length) {
      setSelectedLogs([])
    } else {
      setSelectedLogs(logs.map(log => log.id))
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
    toast.success('Logged out successfully')
  }

  const renderDashboard = () => (
    <div className="dashboard-content">
      <div className="dashboard-welcome">
        <div className="welcome-text">
          <h1>Welcome back, {user?.firstName || 'Admin'}! 👋</h1>
          <p>Here's what's happening with your attendance system today</p>
        </div>
        <div className="welcome-time">
          <div className="time-display">{new Date().toLocaleDateString('en-US', { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
          })}</div>
        </div>
      </div>
      
      {stats && (
        <>
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-icon users">
                <Users size={30} />
              </div>
              <div className="stat-info">
                <h3>{stats.totalUsers || 0}</h3>
                <p>Total Users</p>
                {activity && activity.userGrowth && (
                  <span className="stat-growth positive">↗ +{activity.userGrowth}% this week</span>
                )}
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-icon courses">
                <FileText size={30} />
              </div>
              <div className="stat-info">
                <h3>{stats.totalCourses || 0}</h3>
                <p>Total Courses</p>
                {activity && activity.courseGrowth && (
                  <span className="stat-growth positive">↗ +{activity.courseGrowth}% this week</span>
                )}
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-icon students">
                <Users size={30} />
              </div>
              <div className="stat-info">
                <h3>{stats.totalStudents || 0}</h3>
                <p>Total Students</p>
                {activity && activity.studentGrowth && (
                  <span className="stat-growth positive">↗ +{activity.studentGrowth}% this week</span>
                )}
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-icon attendance">
                <Activity size={30} />
              </div>
              <div className="stat-info">
                <h3>{stats.totalAttendance || 0}</h3>
                <p>Attendance Records</p>
                {activity && activity.attendanceGrowth && (
                  <span className="stat-growth positive">↗ +{activity.attendanceGrowth}% this week</span>
                )}
              </div>
            </div>
          </div>

          {activity && (
            <div className="activity-section">
              <div className="section-header-inline">
                <h2>📊 Today's Activity</h2>
                <span className="live-indicator">
                  <span className="pulse-dot"></span>
                  Live
                </span>
              </div>
              <div className="activity-grid">
                <div className="activity-card">
                  <div className="activity-icon">👥</div>
                  <div className="activity-number">{activity.newUsersToday}</div>
                  <div className="activity-label">New Users</div>
                </div>
                <div className="activity-card">
                  <div className="activity-icon">📚</div>
                  <div className="activity-number">{activity.newCoursesToday}</div>
                  <div className="activity-label">New Courses</div>
                </div>
                <div className="activity-card">
                  <div className="activity-icon">🎓</div>
                  <div className="activity-number">{activity.newStudentsToday}</div>
                  <div className="activity-label">New Students</div>
                </div>
                <div className="activity-card">
                  <div className="activity-icon">✅</div>
                  <div className="activity-number">{activity.attendanceToday}</div>
                  <div className="activity-label">Attendance Marked</div>
                </div>
              </div>
            </div>
          )}

          <div className="quick-actions-section">
            <h2>⚡ Quick Actions</h2>
            <div className="quick-actions-grid">
              <button 
                className="quick-action-btn"
                onClick={() => setActiveSection('users')}
              >
                <Users size={24} />
                <span>Manage Users</span>
              </button>
              <button 
                className="quick-action-btn"
                onClick={() => setActiveSection('logs')}
              >
                <FileText size={24} />
                <span>View Logs</span>
              </button>
              <button 
                className="quick-action-btn"
                onClick={() => setActiveSection('performance')}
              >
                <Activity size={24} />
                <span>System Health</span>
              </button>
              <button 
                className="quick-action-btn"
                onClick={() => setActiveSection('settings')}
              >
                <Settings size={24} />
                <span>Settings</span>
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  )

  const renderLogs = () => (
    <div className="logs-content">
      <div className="logs-header">
        <div>
          <h1>System Logs</h1>
          <p className="section-subtitle">Monitor system activity and error logs in real-time</p>
        </div>
        <div className="logs-actions">
          {selectedLogs.length > 0 && (
            <button onClick={handleDeleteSelected} className="btn btn-danger">
              <Trash2 size={18} />
              Delete Selected ({selectedLogs.length})
            </button>
          )}
          <button onClick={handleDeleteAll} className="btn btn-danger">
            <Trash2 size={18} />
            Delete All
          </button>
          <button onClick={loadLogs} className="btn btn-secondary">
            <RefreshCw size={18} />
            Refresh
          </button>
        </div>
      </div>
      {loading ? (
        <div className="loading-center">
          <div className="spinner"></div>
        </div>
      ) : (
        <>
          <div className="logs-list">
            {logs.length === 0 ? (
              <p className="no-data">No logs found</p>
            ) : (
              <>
                <div className="logs-table-header">
                  <input
                    type="checkbox"
                    checked={selectedLogs.length === logs.length && logs.length > 0}
                    onChange={toggleSelectAll}
                    className="log-checkbox"
                  />
                  <span className="select-all-label">Select All</span>
                </div>
                {logs.map((log) => (
                  <div key={log.id} className={`log-entry ${log.level?.toLowerCase() || 'info'}`}>
                    <div className="log-select">
                      <input
                        type="checkbox"
                        checked={selectedLogs.includes(log.id)}
                        onChange={() => toggleLogSelection(log.id)}
                        className="log-checkbox"
                      />
                    </div>
                    <div className="log-content-wrapper">
                      <div className="log-header-row">
                        <span className="log-level">{log.level || 'INFO'}</span>
                        <span className="log-time">{new Date(log.timestamp).toLocaleString()}</span>
                        <button 
                          onClick={() => handleDeleteLog(log.id)}
                          className="btn-icon-small danger"
                          title="Delete log"
                        >
                          <Trash2 size={16} />
                        </button>
                      </div>
                      <div className="log-message">{log.message}</div>
                      {log.username && (
                        <div className="log-meta">
                          <span>User: {log.username}</span>
                          {log.action && <span>Action: {log.action}</span>}
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </>
            )}
          </div>

          {logsTotalPages > 1 && (
            <div className="pagination">
              <button 
                onClick={() => setLogsPage(p => Math.max(0, p - 1))} 
                disabled={logsPage === 0}
                className="btn btn-secondary"
              >
                <ChevronLeft size={20} />
                Previous
              </button>
              <span className="pagination-info">
                Page {logsPage + 1} of {logsTotalPages}
              </span>
              <button 
                onClick={() => setLogsPage(p => Math.min(logsTotalPages - 1, p + 1))} 
                disabled={logsPage >= logsTotalPages - 1}
                className="btn btn-secondary"
              >
                Next
                <ChevronRight size={20} />
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )

  const renderAnalytics = () => (
    <div className="analytics-content">
      <h1>Analytics</h1>
      <p className="coming-soon">Advanced analytics coming soon...</p>
    </div>
  )

  const renderPerformance = () => (
    <div className="performance-content">
      <h1>Performance Monitoring</h1>
      <p className="section-subtitle">System health and resource usage metrics</p>
      
      {loading ? (
        <div className="loading-center">
          <div className="spinner"></div>
        </div>
      ) : performance ? (
        <>
          <div className="performance-grid">
            <div className="performance-card">
              <h3>💾 Memory Usage</h3>
              <div className="performance-chart">
                <div className="circular-progress">
                  <svg viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="45" fill="none" stroke="#e2e8f0" strokeWidth="10"/>
                    <circle 
                      cx="50" 
                      cy="50" 
                      r="45" 
                      fill="none" 
                      stroke="#667eea" 
                      strokeWidth="10"
                      strokeDasharray={`${performance.memoryUsagePercent * 2.827} 282.7`}
                      strokeLinecap="round"
                      transform="rotate(-90 50 50)"
                    />
                  </svg>
                  <div className="progress-value">{performance.memoryUsagePercent}%</div>
                </div>
              </div>
              <div className="performance-details">
                <div className="detail-row">
                  <span>Used:</span>
                  <span>{performance.usedMemoryMB} MB</span>
                </div>
                <div className="detail-row">
                  <span>Free:</span>
                  <span>{performance.freeMemoryMB} MB</span>
                </div>
                <div className="detail-row">
                  <span>Total:</span>
                  <span>{performance.totalMemoryMB} MB</span>
                </div>
              </div>
            </div>

            <div className="performance-card">
              <h3>⚙️ System Resources</h3>
              <div className="resource-list">
                <div className="resource-item">
                  <div className="resource-label">CPU Cores</div>
                  <div className="resource-value">{performance.availableProcessors}</div>
                </div>
                <div className="resource-item">
                  <div className="resource-label">Active Connections</div>
                  <div className="resource-value">{performance.activeConnections}</div>
                </div>
                <div className="resource-item">
                  <div className="resource-label">Memory Efficiency</div>
                  <div className="resource-value">
                    {performance.memoryUsagePercent < 70 ? 'Excellent' : 
                     performance.memoryUsagePercent < 85 ? 'Good' : 'High'}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <button onClick={loadPerformance} className="btn btn-secondary" style={{ marginTop: '20px' }}>
            <RefreshCw size={18} />
            Refresh Metrics
          </button>
        </>
      ) : (
        <p className="coming-soon">Click refresh to load performance metrics</p>
      )}
    </div>
  )

  const renderUsers = () => (
    <div className="users-content">
      <h1>User Management</h1>
      <p className="coming-soon">User management interface coming soon...</p>
    </div>
  )

  const renderSettings = () => (
    <div className="settings-content">
      <h1>⚙️ Settings & Developer Tools</h1>
      
      <div className="settings-grid">
        {/* Developer Tools Section */}
        <div className="settings-card">
          <div className="card-header">
            <FileText size={24} />
            <h3>API Documentation</h3>
          </div>
          <p className="card-description">
            Interactive API documentation with Swagger UI. Test all endpoints and view request/response schemas.
          </p>
          <a 
            href="http://localhost:8080/swagger-ui.html" 
            target="_blank" 
            rel="noopener noreferrer" 
            className="btn btn-primary"
          >
            <FileText size={18} />
            Open Swagger UI
          </a>
        </div>

        {/* WebSocket Test */}
        <div className="settings-card">
          <div className="card-header">
            <Activity size={24} />
            <h3>WebSocket Testing</h3>
          </div>
          <p className="card-description">
            Test real-time WebSocket connections and notifications. Monitor live events and messages.
          </p>
          <a 
            href="http://localhost:8080/websocket-test.html" 
            target="_blank" 
            rel="noopener noreferrer" 
            className="btn btn-primary"
          >
            <Activity size={18} />
            Open WebSocket Test
          </a>
        </div>

        {/* System Info */}
        <div className="settings-card">
          <div className="card-header">
            <BarChart3 size={24} />
            <h3>System Information</h3>
          </div>
          <div className="info-list">
            <div className="info-item">
              <span className="info-label">Backend URL:</span>
              <span className="info-value">http://localhost:8080</span>
            </div>
            <div className="info-item">
              <span className="info-label">API Version:</span>
              <span className="info-value">v1.0.0</span>
            </div>
            <div className="info-item">
              <span className="info-label">Environment:</span>
              <span className="info-value badge badge-success">Production</span>
            </div>
          </div>
        </div>

        {/* Account Settings */}
        <div className="settings-card">
          <div className="card-header">
            <Users size={24} />
            <h3>Account Settings</h3>
          </div>
          <div className="info-list">
            <div className="info-item">
              <span className="info-label">Name:</span>
              <span className="info-value">{user?.firstName} {user?.lastName}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Email:</span>
              <span className="info-value">{user?.email}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Role:</span>
              <span className="info-value badge badge-primary">Admin</span>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="quick-actions">
        <h3>Quick Actions</h3>
        <div className="actions-grid">
          <button className="action-btn" onClick={loadDashboardStats}>
            <RefreshCw size={20} />
            Refresh Statistics
          </button>
          <button className="action-btn" onClick={() => setActiveSection('logs')}>
            <FileText size={20} />
            View System Logs
          </button>
          <button className="action-btn danger" onClick={handleLogout}>
            <LogOut size={20} />
            Logout
          </button>
        </div>
      </div>
    </div>
  )

  return (
    <div className="admin-dashboard">
      <aside className="sidebar">
        <div className="sidebar-header">
          <h2>👨‍💼 Admin Panel</h2>
          <p>{user?.email}</p>
        </div>

        <nav className="sidebar-nav">
          <button
            className={`nav-link ${activeSection === 'dashboard' ? 'active' : ''}`}
            onClick={() => setActiveSection('dashboard')}
          >
            <LayoutDashboard size={20} />
            Dashboard
          </button>
          <button
            className={`nav-link ${activeSection === 'analytics' ? 'active' : ''}`}
            onClick={() => setActiveSection('analytics')}
          >
            <BarChart3 size={20} />
            Analytics
          </button>
          <button
            className={`nav-link ${activeSection === 'logs' ? 'active' : ''}`}
            onClick={() => setActiveSection('logs')}
          >
            <FileText size={20} />
            System Logs
          </button>
          <button
            className={`nav-link ${activeSection === 'performance' ? 'active' : ''}`}
            onClick={() => setActiveSection('performance')}
          >
            <Activity size={20} />
            Performance
          </button>
          <button
            className={`nav-link ${activeSection === 'users' ? 'active' : ''}`}
            onClick={() => setActiveSection('users')}
          >
            <Users size={20} />
            Users
          </button>
          <button
            className={`nav-link ${activeSection === 'settings' ? 'active' : ''}`}
            onClick={() => setActiveSection('settings')}
          >
            <Settings size={20} />
            Settings
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
        {activeSection === 'dashboard' && renderDashboard()}
        {activeSection === 'analytics' && renderAnalytics()}
        {activeSection === 'logs' && renderLogs()}
        {activeSection === 'performance' && renderPerformance()}
        {activeSection === 'users' && renderUsers()}
        {activeSection === 'settings' && renderSettings()}
      </main>
    </div>
  )
}
