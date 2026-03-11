// 功能：处理normalize
const normalize = (value) => {
  if (!value) {
    return []
  }
  if (Array.isArray(value)) {
    return value
  }
  return [value]
}

// 功能：加载权限
const loadPerms = () => {
  try {
    const raw = localStorage.getItem('perms')
    const perms = raw ? JSON.parse(raw) : []
    return Array.isArray(perms) ? perms : []
  } catch (error) {
    return []
  }
}

// 功能：判断是否拥有权限
const hasPermission = (required) => {
  const requiredPerms = normalize(required)
  if (requiredPerms.length === 0) {
    return true
  }
  const perms = loadPerms()
  return requiredPerms.some((perm) => perms.includes(perm))
}

export const permissionDirective = {
  mounted(el, binding) {
    if (!hasPermission(binding.value)) {
      el.remove()
    }
  },
  updated(el, binding) {
    if (!hasPermission(binding.value)) {
      el.remove()
    }
  },
}
