document.addEventListener("DOMContentLoaded", function () {

    const uploadArea = document.querySelector('.upload-area');
    const fileInput = document.getElementById('reportImages');
    if (uploadArea && fileInput) {
        uploadArea.addEventListener('click', function () {
            fileInput.click();
        });

        fileInput.addEventListener('change', function () {
            const h5Text = uploadArea.querySelector('h5');
            const pText = uploadArea.querySelector('p');

            if (this.files && this.files.length > 0) {
                if (this.files.length > 3) {
                    h5Text.innerHTML = `<span class="text-danger fw-bold">Waduh, Kebanyakan!</span>`;
                    pText.innerHTML = `<span class="text-danger fw-bold">Maksimal 3 foto! Kamu memilih ${this.files.length} foto</span>`;
                    this.value = '';
                } else {
                    let fileNames = Array.from(this.files).map(f => f.name).join(', ');
                    h5Text.innerHTML = `<span class="text-success fw-bold"><i class="bi bi-check-circle"></i> File Terpilih!</span>`;
                    pText.innerHTML = `<span class="text-success fw-bold">${this.files.length} foto siap dikirim:</span><br><small>${fileNames}</small>`;
                }
            } else {
                h5Text.textContent = 'Klik untuk unggah atau seret file di sini';
                h5Text.className = 'h6 fw-bold mb-1';
                pText.textContent = 'PNG, JPG, dan WEBP. Maks 3 foto (Max 5MB/file)';
            }
        });
    }

    const triggerTabList = document.querySelectorAll('#masterDataTab button');
    if (triggerTabList.length > 0) {
        triggerTabList.forEach(triggerEl => {
            triggerEl.addEventListener('click', event => {
                triggerTabList.forEach(el => {
                    el.classList.remove('border-bottom', 'border-3', 'border-primary', 'text-dark');
                    el.classList.add('text-secondary');
                });

                event.target.classList.add('border-bottom', 'border-3', 'border-primary', 'text-dark');
                event.target.classList.remove('text-secondary');
            });
        });
    }

    const modalEditCategory = document.getElementById('modalEditCategory');
    if (modalEditCategory) {
        modalEditCategory.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            const name = button.getAttribute('data-name');
            const desc = button.getAttribute('data-desc');

            modalEditCategory.querySelector('#editCategoryId').value = id;
            modalEditCategory.querySelector('#editCategoryName').value = name;
            modalEditCategory.querySelector('#editCategoryDesc').value = desc;
        })
    }

    const modalEditRoom = document.getElementById('modalEditRoom');
    if (modalEditRoom) {
        modalEditRoom.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            const building = button.getAttribute('data-building');
            const name = button.getAttribute('data-name');
            const floor = button.getAttribute('data-floor');
            const code = button.getAttribute('data-code');

            modalEditRoom.querySelector('#editRoomId').value = id;
            modalEditRoom.querySelector('#editRoomBuilding').value = building;
            modalEditRoom.querySelector('#editRoomName').value = name;
            modalEditRoom.querySelector('#editRoomFloor').value = floor;
            modalEditRoom.querySelector('#editRoomCode').value = code;
        })
    }

    const modalDeleteCategory = document.getElementById('modalDeleteCategory');
    if (modalDeleteCategory) {
        modalDeleteCategory.addEventListener('show.bs.modal', function(event) {
           const button = event.relatedTarget;
           const id = button.getAttribute('data-id');
           const name = button.getAttribute('data-name');

           modalDeleteCategory.querySelector('#deleteCategoryId').value = id;
           modalDeleteCategory.querySelector('#deleteCategoryNameDisplay').textContent = name;
        });
    }

    const modalDeleteRoom = document.getElementById('modalDeleteRoom');
    if (modalDeleteRoom) {
        modalDeleteRoom.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            const name = button.getAttribute('data-name');

            modalDeleteRoom.querySelector('#deleteRoomId').value = id;
            modalDeleteRoom.querySelector('#deleteRoomNameDisplay').textContent = name;
        });
    }
});