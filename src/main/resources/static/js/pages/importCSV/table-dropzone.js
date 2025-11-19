const dropzone = document.getElementById('dropzone');
const fileInput = document.getElementById('file-input');
const fileNameLabel = document.getElementById('file-name');
const submitBtn = document.getElementById('submit-btn');

// klik area drop = buka file dialog
dropzone.addEventListener('click', () => {
    fileInput.click();
});

// kalau pilih file via dialog
fileInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    handleFile(file);
});

// drag over
dropzone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropzone.classList.add('border-blue-500', 'bg-blue-50');
});

dropzone.addEventListener('dragleave', (e) => {
    e.preventDefault();
    dropzone.classList.remove('border-blue-500', 'bg-blue-50');
});

// drop file
dropzone.addEventListener('drop', (e) => {
    e.preventDefault();
    dropzone.classList.remove('border-blue-500', 'bg-blue-50');

    const file = e.dataTransfer.files[0];
    if (file) {
        // Set file ke input hidden
        const dataTransfer = new DataTransfer();
        dataTransfer.items.add(file);
        fileInput.files = dataTransfer.files;

        handleFile(file);
    }
});

function handleFile(file) {
    if (!file) return;

    if (!file.name.toLowerCase().endsWith('.csv')) {
        alert('Please upload CSV file only (.csv)');
        fileInput.value = '';
        fileNameLabel.textContent = 'No file selected';
        submitBtn.disabled = true;
        return;
    }

    fileNameLabel.textContent = `Selected: ${file.name}`;
    submitBtn.disabled = false;
}

document.addEventListener('DOMContentLoaded', function () {
    const table = document.getElementById('transactions-table');

    if (table) {
        $('#transactions-table').DataTable({
            pageLength: 10,                   // default 25 row per page
            lengthMenu: [10, 25, 50, 100],    // opsi dropdown
            ordering: true,                   // enable sorting
            searching: true,                  // enable search box
            // biar ga auto sort kolom pertama
            order: [],
            language: {
                lengthMenu: "Show _MENU_ rows",
                search: "Search:",
                info: "Showing _START_ to _END_ of _TOTAL_ rows",
                paginate: {
                    previous: "Prev",
                    next: "Next"
                }
            }
        });
    }
});
