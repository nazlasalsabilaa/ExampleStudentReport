document.addEventListener("DOMContentLoaded", function() {
    const togglePasswordButtons = document.querySelectorAll('.input-group .cursor-pointer');

    togglePasswordButtons.forEach(button => {
        button.addEventListener('click', function() {
            const passwordInput = this.previousElementSibling;
            const icon = this.querySelector('i');

            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.classList.remove('bi-eye');
                icon.classList.add('bi-eye-slash');
            } else {
                passwordInput.type = 'password';
                icon.classList.remove('bi-eye-slash');
                icon.classList.add('bi-eye');
            }
        });
    })

    const loginForm = document.getElementById("loginForm");

    if (loginForm) {
        loginForm.addEventListener('submit', async function (e) {
            e.preventDefault();

            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            try {
                const response = await fetch('/api/v1/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        email: email,
                        password: password,
                    })
                });

                const result = await response.json();

                if (response.ok && result.success) {
                    window.location.href = "/dashboard";
                } else {
                    alert("Login failed : " + (result.message || "Unknown error"));
                }

            } catch (error) {
                console.log('API Error: ', error);
                alert('A network error has occurred');
            }
        });
    }

    const registerForm = document.getElementById("registerForm");

    if (registerForm) {
        registerForm.addEventListener('submit', async function (e) {
            e.preventDefault();

            const name = document.getElementById('name').value;
            const nim = document.getElementById('nim').value;
            const faculty = document.getElementById('faculty').value;
            const major = document.getElementById('major').value;
            const year = parseInt(document.getElementById('year').value, 10);
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (password !== confirmPassword) {
                alert("Passwords do not match");
                return;
            }

            try {
                const response = await fetch('/api/v1/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        name: name,
                        email: email,
                        password: password,
                        nim: nim,
                        faculty: faculty,
                        major: major,
                        year: year
                    })
                });

                const result = await response.json();

                if (response.ok && result.success) {
                    alert('Registration successful! Please log in.');
                    window.location.href = '/login';
                } else {
                    alert('Registration failed: ' + (result.message || 'Unknown error'));
                }
            } catch (error) {
                console.error('API Error:', error);
                alert('A network error occurred.');
            }
        });
    }
});