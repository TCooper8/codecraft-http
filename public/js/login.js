$(document).ready(function() {

function getProfile () {
  console.log('Loading user %s', state.userId)
  $.ajax({
    url: '/user/' + state.userId,
    type: 'get',
    contentType: 'application/json; charset=utf-8',
    dataType: 'json',
    beforeSend: function(req) {
      req.setRequestHeader('Authorization', state.token)
    },
    success: function(data, status, response) {
      if (data.error) {
        return alert(data.error)
      }
      else if (!state.token) {
        window.location = '/index.html'
        return
      }

      state.token = data.token
      state.userData = data

      console.log('Got user data %j', data)
      window.location = '/userPage'
    },
    error: function(error) {
      alert(error.responseText)
    }
  })
}

$('#login').click(function() {
  console.log('Logging in...')
  $.ajax({
    url: '/login',
    type: 'POST',
    contentType: 'application/json; charset=utf-8',
    data: JSON.stringify({
      email: $('#inputEmail').val(),
      password: $('#inputPassword').val()
    }),
    dataType: 'json',
    success: function(data, status, response) {
      if (data.error) {
        return alert(data.error)
      }
      else {
        console.dir(data)
        var roles = data.roles
        var getUserRole = roles['user.getuser']
        if (getUserRole.length === 0) {
          throw new Error('This user has no getuser permissions')
        }
        else if (getUserRole.length > 1) {
          throw new Error('This user has multiple user.getuser permissions defined')
        }
        var userId = getUserRole[0]

        state.token = data.token
        state.userId = userId
        state.roles = data.roles
        getProfile()
      }
    },
    failure: function(error) {
      alert(error)
    }
  })
})

$('#logout').click(function() {
  console.log('Logging out')
})

console.log('loaded')
})
