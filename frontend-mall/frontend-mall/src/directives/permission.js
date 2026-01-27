const normalize = (value) => {
  if (!value) {
    return []
  }
  if (Array.isArray(value)) {
    return value
  }
  return [value]
}

const loadPerms = () => {
  try {
    const raw = localStorage.getItem('perms')
    const perms = raw ? JSON.parse(raw) : []
    return Array.isArray(perms) ? perms : []
  } catch (error) {
    return []
  }
}

const hasPermission = (required) => {
  const roleKey = localStorage.getItem('roleKey')
  if (roleKey === 'ADMIN') {
    return true
  }
  const perms = loadPerms()
  return normalize(required).some((perm) => perms.includes(perm))
}

export const permissionDirective = {
  mounted(el, binding) {
    if (!hasPermission(binding.value)) {
      el.remove()
    }
  },
}
