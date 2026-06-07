document.addEventListener("DOMContentLoaded", function () {

    const uploadArea = document.querySelector('.upload-area');
    const fotoInput = document.getElementById('fotoKerusakan');
    if (uploadArea && fotoInput) {
        uploadArea.addEventListener('click', function () {
            fotoInput.click();
        })
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