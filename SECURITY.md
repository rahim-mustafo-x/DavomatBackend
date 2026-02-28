# 🔒 Security Guide

## Overview

This document outlines security best practices and configuration for the Davomat application.

## ⚠️ Critical Security Steps

### 1. Environment Variables

**NEVER commit sensitive data to git!** All sensitive configuration uses environment variables.

Create a `.env` file in the project root (already in .gitignore):

```bash
# Copy the example file
cp .env.example .env

# Edit with your actual values
nano .env
```

Required variables:
- `DB_PASSWORD` - Database password
- `MAIL_USERNAME` - Email address for notifications
- `MAIL_PASSWORD` - Gmail app password (not your regular password)
- `JWT_SECRET` - Strong random string for JWT signing

### 2. Generate Secure JWT Secret

```bash
# Generate a strong 512-bit secret
openssl rand -base64 64

# Or use this online: https://generate-secret.vercel.app/64
```

Add to your `.env` file:
```
JWT_SECRET=your_generated_secret_here
```

### 3. Gmail App Password

1. Enable 2-Factor Authentication on your Google account
2. Go to: https://myaccount.google.com/apppasswords
3. Generate an app password for "Mail"
4. Use this password in `.env` file (NOT your regular Gmail password)

### 4. Database Security

**Development:**
```properties
DB_PASSWORD=dev_password_123
```

**Production:**
- Use a strong password (16+ characters, mixed case, numbers, symbols)
- Never use default passwords
- Rotate passwords regularly

### 5. Production Checklist

Before deploying to production:

- [ ] Change all default passwords
- [ ] Generate new JWT secret (512-bit minimum)
- [ ] Use environment-specific `.env` files
- [ ] Enable HTTPS/TLS
- [ ] Configure firewall rules
- [ ] Set up database backups
- [ ] Enable audit logging
- [ ] Review CORS settings
- [ ] Disable debug/development features
- [ ] Update dependencies regularly

## 🛡️ Security Features

### Authentication
- JWT-based authentication with configurable expiration
- BCrypt password hashing with salt
- Token invalidation on password change
- Secure password reset flow

### Authorization
- Role-based access control (RBAC)
- Admin, Teacher, Student roles
- Endpoint-level security
- Method-level security with @PreAuthorize

### Data Protection
- SQL injection prevention (JPA parameterized queries)
- XSS protection (input sanitization)
- CSRF protection
- Secure headers configuration

### WebSocket Security
- JWT token validation for WebSocket connections
- User-specific message queues
- Connection authentication required

## 🔐 Configuration Files

### application.properties
Uses environment variables with safe defaults:
```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/davomat_db}
spring.datasource.username=${DB_USERNAME:davomat_user}
spring.datasource.password=${DB_PASSWORD:changeme}
jwt.secret=${JWT_SECRET:change-in-production}
```

### docker-compose.yml
Reads from `.env` file:
```yaml
environment:
  DB_PASSWORD: ${DB_PASSWORD:-changeme}
  MAIL_PASSWORD: ${MAIL_PASSWORD:-your-app-password}
  JWT_SECRET: ${JWT_SECRET:-change-in-production}
```

## 🚨 What NOT to Do

❌ **NEVER** commit these files:
- `.env`
- `application-prod.properties`
- Any file with real credentials

❌ **NEVER** hardcode:
- Passwords
- API keys
- JWT secrets
- Database credentials
- Email passwords

❌ **NEVER** use in production:
- Default passwords
- Weak JWT secrets
- Debug mode enabled
- SQL logging enabled

## 📝 Incident Response

If credentials are exposed:

1. **Immediately** rotate all affected credentials
2. Revoke compromised JWT tokens
3. Check logs for unauthorized access
4. Update `.env` file with new credentials
5. Redeploy application
6. Notify affected users if necessary

## 🔍 Security Auditing

Regular security checks:

```bash
# Check for exposed secrets in git history
git log -p | grep -i "password\|secret\|key"

# Scan dependencies for vulnerabilities
./mvnw dependency-check:check

# Frontend security audit
cd frontend && npm audit
```

## 📞 Reporting Security Issues

If you discover a security vulnerability:

1. **DO NOT** create a public GitHub issue
2. Email: security@davomat.uz
3. Include:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

## 📚 Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [PostgreSQL Security](https://www.postgresql.org/docs/current/security.html)

---

**Remember: Security is not a feature, it's a requirement!**

*Last Updated: February 2026*
