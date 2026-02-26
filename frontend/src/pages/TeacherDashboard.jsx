import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import { 
  BookOpen, 
  Users, 
  ClipboardCheck, 
  LogOut,
  Plus,
  Edit,
  Trash2,
  Eye,
  RefreshCw,
  ChevronLeft,
  ChevronRight
} from 'lucide-react'
import api from '../api/axios'
import { useAuthStore } from '../store/authStore'
import { ROLES } from '../constants/roles'
import './TeacherDashboard.css'

export default function TeacherDashboard() {
  const [activeSection, setActiveSection] = useState('courses')
  const [courses, setCourses] = useState([])
  const [students, setStudents] = useState([])
  const [groups, setGroups] = useState([])
  const [selectedCourse, setSelectedCourse] = useState(null)
  const [selectedGroup, setSelectedGroup] = useState(null)
  const [loading, setLoading] = useState(false)
  const [currentPage, setCurrentPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [showModal, setShowModal] = useState(false)
  const [modalType, setModalType] = useState('') // 'course', 'student', 'group'
  const [editingItem, setEditingItem] = useState(null)
  
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()

  // Verify role
  useEffect(() => {
    if (user?.role !== ROLES.TEACHER) {
      toast.error('Access denied')
      navigate('/')
    }
  }, [user, navigate])

  useEffect(() => {
    if (activeSection === 'courses') {
      loadCourses()
    } else if (activeSection === 'students') {
      loadStudents()
    } else if (activeSection === 'groups') {
      loadGroups()
    }
  }, [activeSection, currentPage])

  const loadCourses = async () => {
    setLoading(true)
    try {
      const response = await api.get(`/api/course/getAllCourses?page=${currentPage}&size=10`)
      const data = response.data
      setCourses(data.content || [])
      setTotalPages(data.totalPages || 0)
    } catch (error) {
      toast.error('Failed to load courses')
    } finally {
      setLoading(false)
    }
  }

  const loadGroups = async () => {
    setLoading(true)
    try {
      if (selectedCourse) {
        const response = await api.get(`/api/group/findByCourseId/${selectedCourse.id}?page=${currentPage}&size=10`)
        const data = response.data
        setGroups(data.content || [])
        setTotalPages(data.totalPages || 0)
      } else if (courses.length > 0) {
        const response = await api.get(`/api/group/findByCourseId/${courses[0].id}?page=${currentPage}&size=10`)
        const data = response.data
        setGroups(data.content || [])
        setTotalPages(data.totalPages || 0)
      }
    } catch (error) {
      toast.error('Failed to load groups')
    } finally {
      setLoading(false)
    }
  }

  const loadStudents = async () => {
    setLoading(true)
    try {
      if (selectedGroup) {
        const response = await api.get(`/api/student/findByGroupId/${selectedGroup.id}?page=${currentPage}&size=10`)
        const data = response.data
        setStudents(data.content || [])
        setTotalPages(data.totalPages || 0)
      } else if (groups.length > 0) {
        const response = await api.get(`/api/student/findByGroupId/${groups[0].id}?page=${currentPage}&size=10`)
        const data = response.data
        setStudents(data.content || [])
        setTotalPages(data.totalPages || 0)
      }
    } catch (error) {
      toast.error('Failed to load students')
    } finally {
      setLoading(false)
    }
  }

  const handleCreateCourse = async (formData) => {
    try {
      await api.post('/api/course/create', formData)
      toast.success('Course created successfully')
      setShowModal(false)
      loadCourses()
    } catch (error) {
      toast.error('Failed to create course')
    }
  }

  const handleUpdateCourse = async (formData) => {
    try {
      await api.put('/api/course/update', { ...formData, id: editingItem.id })
      toast.success('Course updated successfully')
      setShowModal(false)
      setEditingItem(null)
      loadCourses()
    } catch (error) {
      toast.error('Failed to update course')
    }
  }

  const handleDeleteCourse = async (id) => {
    if (!window.confirm('Are you sure you want to delete this course?')) return
    
    try {
      await api.delete(`/api/course/delete/${id}`)
      toast.success('Course deleted successfully')
      loadCourses()
    } catch (error) {
      toast.error('Failed to delete course')
    }
  }

  const handleCreateGroup = async (formData) => {
    try {
      await api.post('/api/group/create', formData)
      toast.success('Group created successfully')
      setShowModal(false)
      loadGroups()
    } catch (error) {
      toast.error('Failed to create group')
    }
  }

  const handleUpdateGroup = async (formData) => {
    try {
      await api.put('/api/group/update', { ...formData, id: editingItem.id })
      toast.success('Group updated successfully')
      setShowModal(false)
      setEditingItem(null)
      loadGroups()
    } catch (error) {
      toast.error('Failed to update group')
    }
  }

  const handleDeleteGroup = async (id) => {
    if (!window.confirm('Are you sure you want to delete this group?')) return
    
    try {
      await api.delete(`/api/group/delete/${id}`)
      toast.success('Group deleted successfully')
      loadGroups()
    } catch (error) {
      toast.error('Failed to delete group')
    }
  }

  const handleCreateStudent = async (formData) => {
    try {
      await api.post('/api/student/addStudent', formData)
      toast.success('Student added successfully')
      setShowModal(false)
      loadStudents()
    } catch (error) {
      toast.error('Failed to add student')
    }
  }

  const handleUpdateStudent = async (formData) => {
    try {
      await api.put('/api/student/editStudent', { ...formData, id: editingItem.id })
      toast.success('Student updated successfully')
      setShowModal(false)
      setEditingItem(null)
      loadStudents()
    } catch (error) {
      toast.error('Failed to update student')
    }
  }

  const handleDeleteStudent = async (id) => {
    if (!window.confirm('Are you sure you want to delete this student?')) return
    
    try {
      await api.delete(`/api/student/deleteStudent/${id}`)
      toast.success('Student deleted successfully')
      loadStudents()
    } catch (error) {
      toast.error('Failed to delete student')
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
    toast.success('Logged out successfully')
  }

  const openModal = (type, item = null) => {
    setModalType(type)
    setEditingItem(item)
    setShowModal(true)
  }

  const renderCourses = () => (
    <div className="section-content">
      <div className="section-header">
        <h2>My Courses</h2>
        <button onClick={() => openModal('course')} className="btn btn-primary">
          <Plus size={20} />
          Add Course
        </button>
      </div>

      {loading ? (
        <div className="loading-center">
          <div className="spinner"></div>
        </div>
      ) : courses.length === 0 ? (
        <div className="empty-state">
          <BookOpen size={60} />
          <p>No courses yet. Create your first course!</p>
        </div>
      ) : (
        <>
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Description</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {courses.map((course) => (
                  <tr key={course.id}>
                    <td>{course.title}</td>
                    <td>{course.description}</td>
                    <td>
                      <div className="action-buttons">
                        <button onClick={() => openModal('course', course)} className="btn-icon" title="Edit">
                          <Edit size={18} />
                        </button>
                        <button onClick={() => handleDeleteCourse(course.id)} className="btn-icon danger" title="Delete">
                          <Trash2 size={18} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <button 
                onClick={() => setCurrentPage(p => Math.max(0, p - 1))} 
                disabled={currentPage === 0}
                className="btn btn-secondary"
              >
                <ChevronLeft size={20} />
                Previous
              </button>
              <span>Page {currentPage + 1} of {totalPages}</span>
              <button 
                onClick={() => setCurrentPage(p => Math.min(totalPages - 1, p + 1))} 
                disabled={currentPage >= totalPages - 1}
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

  const renderStudents = () => (
    <div className="section-content">
      <div className="section-header">
        <h2>Students</h2>
        <button onClick={() => openModal('student')} className="btn btn-primary">
          <Plus size={20} />
          Add Student
        </button>
      </div>

      {loading ? (
        <div className="loading-center">
          <div className="spinner"></div>
        </div>
      ) : students.length === 0 ? (
        <div className="empty-state">
          <Users size={60} />
          <p>No students yet. Add students to your groups!</p>
        </div>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Full Name</th>
                <th>Phone Number</th>
                <th>Group ID</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {students.map((student) => (
                <tr key={student.id}>
                  <td>{student.fullName}</td>
                  <td>{student.phoneNumber}</td>
                  <td>{student.groupId}</td>
                  <td>
                    <div className="action-buttons">
                      <button onClick={() => openModal('student', student)} className="btn-icon" title="Edit">
                        <Edit size={18} />
                      </button>
                      <button onClick={() => handleDeleteStudent(student.id)} className="btn-icon danger" title="Delete">
                        <Trash2 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )

  const renderGroups = () => (
    <div className="section-content">
      <div className="section-header">
        <h2>Groups</h2>
        <button onClick={() => openModal('group')} className="btn btn-primary">
          <Plus size={20} />
          Add Group
        </button>
      </div>

      {loading ? (
        <div className="loading-center">
          <div className="spinner"></div>
        </div>
      ) : groups.length === 0 ? (
        <div className="empty-state">
          <Users size={60} />
          <p>No groups yet. Create groups for your courses!</p>
        </div>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Course ID</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {groups.map((group) => (
                <tr key={group.id}>
                  <td>{group.name}</td>
                  <td>{group.courseId}</td>
                  <td>
                    <div className="action-buttons">
                      <button onClick={() => openModal('group', group)} className="btn-icon" title="Edit">
                        <Edit size={18} />
                      </button>
                      <button onClick={() => handleDeleteGroup(group.id)} className="btn-icon danger" title="Delete">
                        <Trash2 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )

  const renderAttendance = () => (
    <div className="section-content">
      <div className="section-header">
        <h2>Attendance</h2>
      </div>
      <div className="empty-state">
        <ClipboardCheck size={60} />
        <p>Attendance tracking coming soon...</p>
      </div>
    </div>
  )

  return (
    <div className="teacher-dashboard">
      <aside className="sidebar">
        <div className="sidebar-header">
          <h2>👨‍🏫 Teacher Panel</h2>
          <p>{user?.email}</p>
        </div>

        <nav className="sidebar-nav">
          <button
            className={`nav-link ${activeSection === 'courses' ? 'active' : ''}`}
            onClick={() => { setActiveSection('courses'); setCurrentPage(0); }}
          >
            <BookOpen size={20} />
            Courses
          </button>
          <button
            className={`nav-link ${activeSection === 'groups' ? 'active' : ''}`}
            onClick={() => { setActiveSection('groups'); setCurrentPage(0); }}
          >
            <Users size={20} />
            Groups
          </button>
          <button
            className={`nav-link ${activeSection === 'students' ? 'active' : ''}`}
            onClick={() => { setActiveSection('students'); setCurrentPage(0); }}
          >
            <Users size={20} />
            Students
          </button>
          <button
            className={`nav-link ${activeSection === 'attendance' ? 'active' : ''}`}
            onClick={() => setActiveSection('attendance')}
          >
            <ClipboardCheck size={20} />
            Attendance
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
        {activeSection === 'groups' && renderGroups()}
        {activeSection === 'students' && renderStudents()}
        {activeSection === 'attendance' && renderAttendance()}
      </main>

      {showModal && (
        <FormModal
          type={modalType}
          item={editingItem}
          courses={courses}
          groups={groups}
          onClose={() => { setShowModal(false); setEditingItem(null); }}
          onSubmit={(data) => {
            if (modalType === 'course') {
              editingItem ? handleUpdateCourse(data) : handleCreateCourse(data)
            } else if (modalType === 'group') {
              editingItem ? handleUpdateGroup(data) : handleCreateGroup(data)
            } else if (modalType === 'student') {
              editingItem ? handleUpdateStudent(data) : handleCreateStudent(data)
            }
          }}
        />
      )}
    </div>
  )
}

// Form Modal Component
function FormModal({ type, item, courses, groups, onClose, onSubmit }) {
  const [formData, setFormData] = useState(() => {
    if (type === 'course') {
      return {
        title: item?.title || '',
        description: item?.description || ''
      }
    } else if (type === 'group') {
      return {
        name: item?.name || '',
        courseId: item?.courseId || (courses[0]?.id || '')
      }
    } else if (type === 'student') {
      return {
        fullName: item?.fullName || '',
        phoneNumber: item?.phoneNumber || '',
        groupId: item?.groupId || (groups[0]?.id || '')
      }
    }
    return {}
  })

  const handleSubmit = (e) => {
    e.preventDefault()
    onSubmit(formData)
  }

  const getTitle = () => {
    if (type === 'course') return item ? 'Edit Course' : 'Create Course'
    if (type === 'group') return item ? 'Edit Group' : 'Create Group'
    if (type === 'student') return item ? 'Edit Student' : 'Add Student'
    return 'Form'
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h2>{getTitle()}</h2>
        <form onSubmit={handleSubmit}>
          {type === 'course' && (
            <>
              <div className="form-group">
                <label>Title</label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  rows="4"
                  required
                />
              </div>
            </>
          )}

          {type === 'group' && (
            <>
              <div className="form-group">
                <label>Group Name</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Course</label>
                <select
                  value={formData.courseId}
                  onChange={(e) => setFormData({ ...formData, courseId: e.target.value })}
                  required
                >
                  <option value="">Select a course</option>
                  {courses.map((course) => (
                    <option key={course.id} value={course.id}>
                      {course.title}
                    </option>
                  ))}
                </select>
              </div>
            </>
          )}

          {type === 'student' && (
            <>
              <div className="form-group">
                <label>Full Name</label>
                <input
                  type="text"
                  value={formData.fullName}
                  onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Phone Number</label>
                <input
                  type="tel"
                  value={formData.phoneNumber}
                  onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                  placeholder="+998901234567"
                  required
                />
              </div>
              <div className="form-group">
                <label>Group</label>
                <select
                  value={formData.groupId}
                  onChange={(e) => setFormData({ ...formData, groupId: e.target.value })}
                  required
                >
                  <option value="">Select a group</option>
                  {groups.map((group) => (
                    <option key={group.id} value={group.id}>
                      {group.name}
                    </option>
                  ))}
                </select>
              </div>
            </>
          )}

          <div className="modal-actions">
            <button type="button" onClick={onClose} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              {item ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
