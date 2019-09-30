export function findAllAuthorities() {
  return fetch('/api/authorities').then(res => res.json())
}

export function findUsersMe(page, size) {
  return fetch(`/api/users/me`).then(res => {
    if (res.ok) {
      return res.json()
    } else {
      return res.json()
    }
  })
}

export function findAllUsers(search, page, size) {
  return fetch(`/api/users?search=${search}&page=${page}&size=${size}`).then(res => {
    if (res.ok) {
      return res.json().then(json => ({
        list: json,
        count: parseInt(res.headers.get('x-total')),
      }))
    } else {
      return res.json()
    }
  })
}

export function findAllUserByCodes(codes) {
  const opts = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
    },
    body: JSON.stringify(codes),
  }
  return fetch(`/api/users/search`, opts).then(res => res.json())
}
export function findUserByCode(code) {
  const opts = {
    method: 'GET',
  }
  return fetch(`/api/users/${code}`, opts).then(res => res.json())
}

export function createUser(item) {
  const opts = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
    },
    body: JSON.stringify(item),
  }
  return fetch('/api/users', opts).then(res => res.json())
}

export function updateUser(code, item) {
  const opts = {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
    },
    body: JSON.stringify(item),
  }
  return fetch(`/api/users/${code}`, opts).then(res => res.json())
}

export function deleteUser(code) {
  const opts = {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
    },
  }
  return fetch(`/api/users/${code}`, opts).then(res => res.json())
}

export function resetUserPassword(code) {
  const opts = {
    method: 'PUT',
  }
  return fetch(`/api/users/${code}/reset-password`, opts).then(res => res.json())
}

export default {
  findUsersMe,
  findAllAuthorities,
  findAllUsers,
  findUserByCode,
  findAllUserByCodes,
  createUser,
  updateUser,
  deleteUser,
  resetUserPassword,
}
