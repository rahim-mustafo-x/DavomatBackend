// Role constants - must match backend Strings.java
export const ROLES = {
  ADMIN: 'ROLE_ADMIN',
  TEACHER: 'ROLE_TEACHER',
  STUDENT: 'ROLE_STUDENT'
}

// Helper functions
export const isAdmin = (user) => user?.role === ROLES.ADMIN
export const isTeacher = (user) => user?.role === ROLES.TEACHER
export const isStudent = (user) => user?.role === ROLES.STUDENT

export const getRoleName = (role) => {
  switch (role) {
    case ROLES.ADMIN:
      return 'Admin'
    case ROLES.TEACHER:
      return 'Teacher'
    case ROLES.STUDENT:
      return 'Student'
    default:
      return 'Unknown'
  }
}

export const getDashboardPath = (role) => {
  switch (role) {
    case ROLES.ADMIN:
      return '/admin'
    case ROLES.TEACHER:
      return '/teacher'
    case ROLES.STUDENT:
      return '/student'
    default:
      return '/'
  }
}
