document.addEventListener("DOMContentLoaded", function() {
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
                        faculty: "Fakultas",
                        major: "Prodi",
                        year: 2024
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