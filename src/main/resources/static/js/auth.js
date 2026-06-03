document.addEventListener("DOMContentLoaded", function() {
    const togglePasswordButtons = document.querySelectorAll('.input-group .cursor-pointer');

    togglePasswordButtons.forEach(button => {
        button.addEventListener('click', function() {
            const passwordInput = this.previousElementSibling;
            const svgIcon = this.querySelector('svg');

            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                svgIcon.innerHTML = `
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                    <line x1="1" y1="1" x2="23" y2="23"></line>
                `;
            } else {
                passwordInput.type = 'password';
                svgIcon.innerHTML = `
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                `;
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