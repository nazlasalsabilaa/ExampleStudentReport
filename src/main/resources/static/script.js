document.addEventListener("DOMContentLoaded", function () {

    const uploadArea = document.querySelector('.upload-area');
    const fotoInput = document.getElementById('fotoKerusakan');

    if (uploadArea && fotoInput) {
        uploadArea.addEventListener('click', function () {
            fotoInput.click();
        })
    }
});